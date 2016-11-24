/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.database.iomodules.dhis.exporter;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.ShowException;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Turns a given survey into its corresponding events+datavalues.
 */
public class ConvertToSDKVisitor implements IConvertToSDKVisitor {

    private final static String TAG = ".ConvertToSDKVisitor";

    /**
     * Context required to recover magic UID for mainScore dataElements
     */
    Context context;

    /**
     * List of surveys that are going to be pushed
     */
    List<Survey> surveys;

    /**
     * List of events that are going to be pushed
     */
    List<Event> events;

    ConvertToSDKVisitor(Context context) {
        this.context = context;
        surveys = new ArrayList<>();
        events = new ArrayList<>();
    }

    public static void logEmptySurveyException(Survey survey) {
        PhoneMetaData phoneMetaData = Session.getPhoneMetaData();
        String info = String.format("Survey: %s\nPhoneMetaData: %s\nAPI: %s",
                survey.toString(),
                phoneMetaData == null ? "" : phoneMetaData.getPhone_metaData(),
                Build.VERSION.RELEASE
        );
        Crashlytics.logException(new Throwable(info));
    }

    @Override
    public void visit(Survey survey) throws Exception {

        //Precondition
        if (isEmpty(survey)) {
            survey.delete();
            return;
        }

        if (Survey.countSurveysByCompletiondate(survey.getCompletionDate()) > 1) {
            Log.d(TAG, String.format("Delete repeated survey", survey.toString()));
            survey.delete();
            return;
        }

        Log.d(TAG, String.format("Creating event for survey (%d) ...", survey.getId_survey()));
        Event event = buildEvent(survey);

        //Turn question values into dataValues
        Log.d(TAG, "Creating datavalues from questions...");
        for (Value value : survey.getValues()) {
            buildAndSaveDataValue(value.getQuestion().getUid(), value.getValue(), event);
        }

        Log.d(TAG, "Saving control dataelements");
        buildControlDataElements(survey, event);


        if (Survey.countSurveysByCompletiondate(survey.getCompletionDate()) > 1) {
            Log.d(TAG, String.format("Delete repeated survey", survey.toString()));
            survey.delete();
            event.delete();
            return;
        }

        //Annotate both objects to update its state once the process is over
        annotateSurveyAndEvent(survey, event);
    }

    private boolean isEmpty(Survey survey) {
        if (survey == null) {
            return true;
        }

        List<Value> values = survey.getValuesFromDB();
        if (values == null || values.isEmpty()) {
            logEmptySurveyException(survey);
            return true;
        }
        return false;
    }

    /**
     * Builds several datavalues from the mainScore of the survey
     */
    private void buildControlDataElements(Survey survey, Event event) {
        //save phonemetadata
        PhoneMetaData phoneMetaData = Session.getPhoneMetaData();
        buildAndSaveDataValue(PushClient.PHONEMETADA_UID, phoneMetaData.getPhone_metaData(), event);

        //save Time capture
        if (PushClient.DATETIME_CAPTURE_UID != null && !PushClient.DATETIME_CAPTURE_UID.equals(
                "")) {
            buildAndSaveDataValue(PushClient.DATETIME_CAPTURE_UID,
                    EventExtended.format(survey.getCompletionDate(),
                            EventExtended.COMPLETION_DATE_FORMAT), event);
        }

        //save Time Sent
        if (PushClient.DATETIME_SENT_UID != null && !PushClient.DATETIME_SENT_UID.equals("")) {
            buildAndSaveDataValue(PushClient.DATETIME_SENT_UID,
                    EventExtended.format(new Date(), EventExtended.COMPLETION_DATE_FORMAT), event);
        }
    }

    /**
     * Adds value in Datavalue
     *
     * @param UID   is the dataElement uid
     * @param value is the value
     */
    private void buildAndSaveDataValue(String UID, String value, Event event) {
        DataValue dataValue = new DataValue();
        dataValue.setDataElement(UID);
        dataValue.setLocalEventId(event.getLocalId());
        dataValue.setEvent(event.getEvent());
        dataValue.setProvidedElsewhere(false);
        if (Session.getUser() != null) {
            dataValue.setStoredBy(Session.getUser().getName());
        }
        dataValue.setValue(value);
        dataValue.save();
    }

    /**
     * Builds an event from a survey
     */
    private Event buildEvent(Survey survey) throws Exception {
        Event event = new Event();

        event.setStatus(Event.STATUS_COMPLETED);
        event.setFromServer(false);
        event.setOrganisationUnitId(getSafeOrgUnitUID(survey));
        event.setProgramId(survey.getTabGroup().getProgram().getUid());
        event.setProgramStageId(survey.getTabGroup().getUid());
        event = updateEventLocation(survey, event);
        event = updateEventDates(survey, event);
        Log.d(TAG, "Saving event " + event.toString());
        event.save();
        return event;
    }

    private String getSafeOrgUnitUID(Survey survey) {
        OrgUnit orgUnit = survey.getOrgUnit();
        if (orgUnit != null) {
            return orgUnit.getUid();
        }

        //A survey might be created with a 2.20 (no orgunit) but push into a 2.22
        return OrgUnit.findUIDByName(PreferencesState.getInstance().getOrgUnit());
    }

    /**
     * Fulfills the dates of the event
     */
    private Event updateEventDates(Survey survey, Event event) {

        //Sent date 'now' (this change will be saves after successful push)
        //currentSurvey.setEventDate(new Date());

        //Creation date is null because it is used by sdk to POST|PUT we always POST a new survey
        event.setLastUpdated(EventExtended.format(survey.getCompletionDate()));
        event.setEventDate(EventExtended.format(survey.getCompletionDate()));
        event.setDueDate(EventExtended.format(survey.getScheduledDate()));
        return event;
    }

    /**
     * Updates the location of the current event that it is being processed
     */
    private Event updateEventLocation(Survey survey, Event event) throws Exception {
        Location lastLocation = LocationMemory.get(survey.getId_survey());

        //No location + not required -> done
        if (lastLocation == null) {
            return event;
        }

        //location -> set lat/lng
        event.setLatitude(lastLocation.getLatitude());
        event.setLongitude(lastLocation.getLongitude());
        return event;
    }

    /**
     * Annotates the survey and event that has been processed
     */
    private void annotateSurveyAndEvent(Survey survey, Event event) {
        surveys.add(survey);
        events.add(event);

        Log.d(TAG, String.format("%d surveys converted so far", surveys.size()));
    }

    /**
     * Saves changes in the survey (supposedly after a successful push)
     */
    public void saveSurveyStatus(Map<Long, ImportSummary> importSummaryMap) {
        Log.d(TAG, String.format("ImportSummary %d surveys savedSurveyStatus", surveys.size()));
        for (int i = 0; i < surveys.size(); i++) {
            Survey iSurvey = surveys.get(i);
            Event iEvent = events.get(i);
            //Sets all the surveys as completed because the survey has as state: "sending" at
            // this moment and the sending process is finish.
            iSurvey.setStatus(Constants.SURVEY_COMPLETED);
            ImportSummary importSummary = importSummaryMap.get(iEvent.getLocalId());
            FailedItem failedItem = hasConflict(iEvent.getLocalId());
            if (hasImportSummaryErrors(importSummary)) {
                //Sets the survey status as quarantine to prevent wrong importSummaries (F.E. in
                // network failures).
                //This survey will be checked again in the future push to prevent the duplicates
                // in the server.
                iSurvey.setStatus(Constants.SURVEY_QUARANTINE);

                //If the importSummary has a failedItem the survey was saved in the server but
                // never resend, the survey is saved as survey in conflict.
                if (failedItem != null) {
                    List<String> failedUids = getFailedUidQuestion(failedItem.getErrorMessage());
                    if (failedUids != null && failedUids.size() > 0) {
                        iSurvey.setStatus(Constants.SURVEY_CONFLICT);
                        for (String uid : failedUids) {
                            Log.d(TAG, "PUSH process...ImportSummary Conflict in " + uid
                                    + " dataElement. Survey: " + iSurvey.getId_survey());
                        }
                    }
                }
                iSurvey.save();
            } else {
                iSurvey.setStatus(Constants.SURVEY_SENT);
                iSurvey.saveMainScore();
                iSurvey.save();
                Log.d("DpBlank", "ImportSummary Saving survey as completed " + iSurvey + " event "
                        + iEvent.getUid());
            }
            //Generated event must be remove too
            iEvent.delete();
        }
    }

    /**
     * Checks whether the given event contains errors in SDK FailedItem table or has been
     * successful.
     * If not return null, it is becouse this item had a conflict.
     */
    private FailedItem hasConflict(long localId) {
        return new Select()
                .from(FailedItem.class)
                .where(Condition.column(FailedItem$Table.ITEMID)
                        .is(localId)).querySingle();
    }

    /**
     * Checks whether the given importSummary contains errors or has been successful.
     * An import with 0 importedItems is an error too.
     */
    private boolean hasImportSummaryErrors(ImportSummary importSummary) {
        if (importSummary == null) {
            return true;
        }

        if (importSummary.getImportCount() == null) {
            return true;
        }
        return importSummary.getImportCount().getImported() == 0;
    }


    /**
     * Get dataelement fails from errormessage JSON.
     */
    private List<String> getFailedUidQuestion(String responseData) {
        String message = "";
        List<String> uid = new ArrayList<>();
        JSONArray jsonArrayResponse = null;
        JSONObject jsonObjectResponse = null;
        try {
            jsonObjectResponse = new JSONObject(responseData);
            message = jsonObjectResponse.getString("message");
            jsonObjectResponse = new JSONObject(jsonObjectResponse.getString("response"));
            jsonArrayResponse = new JSONArray(jsonObjectResponse.getString("importSummaries"));
            jsonObjectResponse = new JSONObject(jsonArrayResponse.getString(0));
            jsonArrayResponse = new JSONArray(jsonObjectResponse.getString("conflicts"));
            //values
            for (int i = 0; i < jsonArrayResponse.length(); i++) {
                jsonObjectResponse = new JSONObject(jsonArrayResponse.getString(i));
                uid.add(jsonObjectResponse.getString("object"));
            }
        } catch (JSONException e) {
            Log.d(TAG, "Error import summary response: " + responseData);
            e.printStackTrace();
            return null;
        }
        if (message != "") {
            ShowException.showError(message, PreferencesState.getInstance().getContext());
        }
        return uid;
    }
}
