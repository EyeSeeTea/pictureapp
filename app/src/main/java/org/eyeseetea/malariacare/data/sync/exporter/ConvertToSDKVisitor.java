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

package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.exporter.strategies.ConvertToSdkVisitorStrategy;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.entity.pushsummary.PushConflict;
import org.eyeseetea.malariacare.domain.entity.pushsummary.PushReport;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.push.NullEventDateException;
import org.eyeseetea.malariacare.domain.exception.push.PushReportException;
import org.eyeseetea.malariacare.domain.exception.push.PushValueException;
import org.eyeseetea.malariacare.utils.Constants;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    List<SurveyDB> mSurveyDBs;

    /**
     * Map app surveys with sdk events (N to 1)
     */
    Map<Long, EventExtended> events;

    ConvertToSDKVisitor(Context context) {
        this.context = context;
        mSurveyDBs = new ArrayList<>();
        events = new HashMap<>();
    }

    public static void logEmptySurveyException(SurveyDB surveyDB) {
        String info = String.format("Survey: %s\nPhoneMetaData: %s\nAPI: %s",
                surveyDB.toString(),Session.getPhoneMetaDataValue(),
                Build.VERSION.RELEASE
        );
        Crashlytics.logException(new Throwable(info));
    }

    @Override
    public void visit(SurveyDB surveyDB) throws ConversionException {
        EventExtended event = null;
        try {
            //Precondition
            if (isEmpty(surveyDB)) {
                surveyDB.delete();
                return;
            }

            if (SurveyDB.countSurveysByCompletiondate(surveyDB.getCompletionDate()) > 1) {
                Log.d(TAG, String.format("Delete repeated survey", surveyDB.toString()));
                surveyDB.delete();
                return;
            }

            Log.d(TAG, String.format("Creating event for survey (%d) ...", surveyDB.getId_survey()));
            event = buildEvent(surveyDB);

            surveyDB.setEventUid(event.getUid());
            surveyDB.save();
            //Turn question values into dataValues
            Log.d(TAG, "Creating datavalues from questions...");
            for (ValueDB valueDB : surveyDB.getValuesFromDB()) {
                if (valueDB.getQuestionDB().hasDataElement()) {
                    buildAndSaveDataValue(valueDB.getQuestionDB().getUid(), valueDB.getValue(), event);
                }
            }

            Log.d(TAG, "Saving control dataelements");
            buildControlDataElements(surveyDB, event);


            if (SurveyDB.countSurveysByCompletiondate(surveyDB.getCompletionDate()) > 1) {
                Log.d(TAG, String.format("Delete repeated survey", surveyDB.toString()));
                surveyDB.delete();
                event.delete();
                return;
            }

            //Annotate both objects to update its state once the process is over
            annotateSurveyAndEvent(surveyDB, event);
        } catch (NullPointerException e) {
            //If the conversion fails the survey is wrong and will be delete.
            removeSurveyAndEvent(surveyDB, event);
            throw new ConversionException(e);
        }
    }

    private void removeSurveyAndEvent(SurveyDB surveyDB, EventExtended eventExtended) {
        if (eventExtended != null) {
            //remove event from annotated event list and from db
            if (events.containsKey(surveyDB.getId_survey())) {
                events.remove(surveyDB.getId_survey());
            }
            eventExtended.delete();
        }
        if (surveyDB != null) {
            //remove survey from list and from db
            if (mSurveyDBs.contains(surveyDB)) {
                mSurveyDBs.remove(surveyDB);
            }
            surveyDB.delete();
        }
    }

    private boolean isEmpty(SurveyDB surveyDB) {
        if (surveyDB == null) {
            return true;
        }

        List<ValueDB> valueDBs = surveyDB.getValuesFromDB();
        if (valueDBs == null || valueDBs.isEmpty()) {
            logEmptySurveyException(surveyDB);
            return true;
        }
        return false;
    }

    /**
     * Builds several datavalues from the mainScore of the survey
     */
    private void buildControlDataElements(SurveyDB surveyDB, EventExtended event) {
        //save phonemetadata
        buildAndSaveDataValue((PreferencesState.getInstance().getContext().getString(
                R.string.control_data_element_phone_metadata)), Session.getPhoneMetaDataValue(),
                event);

        //save Time capture
        if (PreferencesState.getInstance().getContext().getString(
                R.string.control_data_element_datetime_capture) != null
                && !PreferencesState.getInstance().getContext().getString(
                R.string.control_data_element_datetime_capture).equals(
                "")) {
            buildAndSaveDataValue(PreferencesState.getInstance().getContext().getString(
                    R.string.control_data_element_datetime_capture),
                    EventExtended.format(surveyDB.getCompletionDate(),
                            EventExtended.DHIS2_GMT_DATE_FORMAT), event);
        }

        //save Time Sent
        if (PreferencesState.getInstance().getContext().getString(
                R.string.control_data_element_datetime_sent) != null
                && !PreferencesState.getInstance().getContext().getString(
                R.string.control_data_element_datetime_sent).equals("")) {
            buildAndSaveDataValue(PreferencesState.getInstance().getContext().getString(
                    R.string.control_data_element_datetime_sent),
                    EventExtended.format(new Date(), EventExtended.DHIS2_GMT_DATE_FORMAT),
                    event);
        }
    }

    /**
     * Adds value in Datavalue
     *
     * @param UID   is the dataElement uid
     * @param value is the value
     */
    private void buildAndSaveDataValue(String UID, String value, EventExtended event) {
        DataValueExtended dataValue = new DataValueExtended();
        dataValue.setDataElement(UID);
        dataValue.setEvent(event.getEvent());
        dataValue.setProvidedElsewhere(false);
        if (Session.getUserDB() != null) {
            dataValue.setStoredBy(Session.getUserDB().getName());
        }
        dataValue.setValue(value);
        dataValue.save();
    }

    /**
     * Builds an event from a survey
     */
    private EventExtended buildEvent(SurveyDB surveyDB) {
        EventExtended event = new EventExtended();

        ConvertToSdkVisitorStrategy.setAttributeCategoryOptionsInEvent(event);

        event.setProgramId(surveyDB.getProgramDB().getUid());
        event.setOrganisationUnitId(surveyDB.getOrgUnitDB().getUid());
        event.setStatus(EventExtended.STATUS_COMPLETED);
        event.setOrganisationUnitId(getSafeOrgUnitUID(surveyDB));
        event.setProgramId(surveyDB.getProgramDB().getUid());
        event.setProgramStageId(surveyDB.getProgramDB().getStageUid());
        event = updateEventLocation(surveyDB, event);
        event = updateEventDates(surveyDB, event);
        Log.d(TAG, "Saving event " + event.toString());
        event.save();
        return event;
    }

    private String getSafeOrgUnitUID(SurveyDB surveyDB) {
        OrgUnitDB orgUnitDB = surveyDB.getOrgUnitDB();
        if (orgUnitDB != null) {
            return orgUnitDB.getUid();
        }

        //A survey might be created with a 2.20 (no orgunit) but push into a 2.22
        return OrgUnitDB.findUIDByName(PreferencesState.getInstance().getOrgUnit());
    }

    /**
     * Fulfills the dates of the event
     */
    private EventExtended updateEventDates(SurveyDB surveyDB, EventExtended event) {

        //Sent date 'now' (this change will be saves after successful push)
        //currentSurvey.setEventDate(new Date());

        //Creation date is null because it is used by sdk to POST|PUT we always POST a new survey
        event.setLastUpdated(new DateTime(surveyDB.getCompletionDate().getTime()));
        event.setEventDate(new DateTime(surveyDB.getEventDate().getTime()));
        if (surveyDB.getScheduledDate() != null) {
            event.setDueDate(new DateTime(surveyDB.getScheduledDate().getTime()));
        }
        return event;
    }

    /**
     * Updates the location of the current event that it is being processed
     */
    private EventExtended updateEventLocation(SurveyDB surveyDB, EventExtended event) {
        Location lastLocation = LocationMemory.get(surveyDB.getId_survey());

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
    private void annotateSurveyAndEvent(SurveyDB surveyDB, EventExtended event) {
        mSurveyDBs.add(surveyDB);
        events.put(surveyDB.getId_survey(), event);

        Log.d(TAG, String.format("%d surveys converted so far", mSurveyDBs.size()));
    }

    /**
     * Saves changes in the survey (supposedly after a successful push)
     */
    public void saveSurveyStatus(Map<String, PushReport> pushReportMap, final
    IPushController.IPushControllerCallback callback) {
        Log.d(TAG, String.format("pushReportMap %d surveys savedSurveyStatus", mSurveyDBs.size()));
        for (int i = 0; i < mSurveyDBs.size(); i++) {
            SurveyDB iSurveyDB = mSurveyDBs.get(i);
            //Sets the survey status as quarantine to prevent wrong reports on unexpected exception.
            //F.E. if the app crash unexpected this survey will be checked again in the future push to prevent the duplicates
            // in the server.
            iSurveyDB.setStatus(Constants.SURVEY_QUARANTINE);
            iSurveyDB.save();
            Log.d(TAG, "saveSurveyStatus: Starting saving survey Set Survey status as QUARANTINE"
                    + iSurveyDB.getId_survey() + " eventuid: " + iSurveyDB.getEventUid());
            EventExtended iEvent = new EventExtended(events.get(iSurveyDB.getId_survey()));
            PushReport pushReport = pushReportMap.get(iEvent.getEvent().getUId());
            if (pushReport == null) {
                //the survey was saved as quarantine.
                new PushReportException(
                        "Error saving survey: report is null for this survey: " + iSurveyDB.getId_survey());
                //The loop should continue without throw the Exception.
                continue;
            }
            List<PushConflict> pushConflicts = pushReport.getPushConflicts();

            //If the pushResult has some conflict the survey was saved in the server but
            // never resend, the survey is saved as survey in conflict.
            if (pushConflicts != null && pushConflicts.size() > 0) {
                Log.d(TAG, "saveSurveyStatus: survey conflicts not null "
                        + iSurveyDB.getId_survey());
                for (PushConflict pushConflict : pushConflicts) {
                    iSurveyDB.setStatus(Constants.SURVEY_CONFLICT);
                    iSurveyDB.save();
                    if (pushConflict.getUid() != null) {
                        Log.d(TAG, "saveSurveyStatus: PUSH process...Conflict in "
                                + pushConflict.getUid() +
                                " with error " + pushConflict.getValue()
                                + " dataElement pushing survey: "
                                + iSurveyDB.getId_survey());
                        callback.onInformativeError(new PushValueException(
                                String.format(context.getString(R.string.error_conflict_message),
                                        iEvent.getEvent().getUId(), pushConflict.getUid(),
                                        pushConflict.getValue()) + ""));
                    }
                }
                continue;
            }

            //No errors -> Save and next
            if (pushReport!=null && !pushReport.hasPushErrors()) {
                Log.d(TAG, "saveSurveyStatus: report without errors and status ok "
                        + iSurveyDB.getId_survey());
                if (iEvent.getEventDate() == null || iEvent.getEventDate().equals("")) {
                    //If eventDate is null the event is invalid. The event is sent but we need
                    // inform to the user.
                    callback.onInformativeError(new NullEventDateException(
                            String.format(context.getString(R.string.error_message_push),
                                    iEvent.getEvent())));
                }
                saveSurveyFromImportSummary(iSurveyDB);
            }
        }
    }

    private void saveSurveyFromImportSummary(SurveyDB iSurveyDB) {
        iSurveyDB.setStatus(Constants.SURVEY_SENT);
        iSurveyDB.saveMainScore();
        iSurveyDB.save();
        Log.d("DpBlank", "ImportSummary Saving survey as completed " + iSurveyDB + " event "
                + iSurveyDB.getEventUid());
        Log.d(TAG, "PUSH process...OK. Survey saved");
    }

    public void setSurveysAsQuarantine() {
        for (SurveyDB surveyDB : mSurveyDBs) {
            Log.d(TAG, "Set Survey status as QUARANTINE" + surveyDB.getId_survey());
            Log.d(TAG, "Set Survey status as QUARANTINE" + surveyDB.toString());
            surveyDB.setStatus(Constants.SURVEY_QUARANTINE);
            surveyDB.save();
        }
    }
}
