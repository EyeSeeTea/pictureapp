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
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
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

    private final static String TAG=".ConvertToSDKVisitor";

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

    /**
     * The last survey that it is being translated
     */
    Survey currentSurvey;

    /**
     * The generated event
     */
    Event currentEvent;

    ConvertToSDKVisitor(Context context){
        this.context=context;
        surveys = new ArrayList<>();
        events = new ArrayList<>();
        currentEvent=null;
        currentSurvey=null;
    }

    @Override
    public void visit(Survey survey) throws Exception{
        this.currentEvent=null;
        this.currentSurvey=null;

        //Precondition
        if(isEmpty(survey)){
            survey.delete();
            return;
        }

        if(Survey.countSurveysByCompletiondate(survey.getCompletionDate())>1) {
            return;
        }
        //Turn survey into an event
        this.currentSurvey=survey;

        Log.d(TAG,String.format("Creating event for survey (%d) ...",survey.getId_survey()));
        this.currentEvent=buildEvent();

        //Calculates scores and update survey
        Log.d(TAG,"Registering scores...");
        List<CompositeScore> compositeScores = ScoreRegister.loadCompositeScores(survey);

        //Turn question values into dataValues
        Log.d(TAG,"Creating datavalues from questions...");
        for(Value value:survey.getValues()){
            value.accept(this);
        }

        Log.d(TAG,"Saving control dataelements");
        buildControlDataElements(survey);

        //Annotate both objects to update its state once the process is over
        annotateSurveyAndEvent();
    }

    private boolean isEmpty(Survey survey){
        if(survey==null){
            return true;
        }

        List<Value> values=survey.getValuesFromDB();
        if(values==null || values.isEmpty()){
            logEmptySurveyException(survey);
            return true;
        }
        return false;
    }

    public static void logEmptySurveyException(Survey survey){
        PhoneMetaData phoneMetaData = Session.getPhoneMetaData();
        String info=String.format("Survey: %s\nPhoneMetaData: %s\nAPI: %s",
                survey.toString(),
                phoneMetaData==null?"":phoneMetaData.getPhone_metaData(),
                Build.VERSION.RELEASE
                );
        Crashlytics.logException(new Throwable(info));
    }
    /**
     * Builds several datavalues from the mainScore of the survey
     * @param survey
     */
    private void buildControlDataElements(Survey survey) {
        //save phonemetadata
        PhoneMetaData phoneMetaData= Session.getPhoneMetaData();
        buildAndSaveDataValue(PushClient.TAG_PHONEMETADA,phoneMetaData.getPhone_metaData());

        //save Time capture
        if(PushClient.TAG_DATETIME_CAPTURE!=null && !PushClient.TAG_DATETIME_CAPTURE.equals(""))
            buildAndSaveDataValue(PushClient.TAG_DATETIME_CAPTURE, EventExtended.format(survey.getCompletionDate(), EventExtended.COMPLETION_DATE_FORMAT));

        //save Time Sent
        if(PushClient.TAG_DATETIME_SENT!=null && !PushClient.TAG_DATETIME_SENT.equals(""))
            buildAndSaveDataValue(PushClient.TAG_DATETIME_SENT,  EventExtended.format(new Date(), EventExtended.COMPLETION_DATE_FORMAT));
    }

    /**
     * Adds value in Datavalue
     * @param UID is the dataElement uid
     * @param value is the value
     */
    private void buildAndSaveDataValue(String UID, String value){
        DataValue dataValue=new DataValue();
        dataValue.setDataElement(UID);
        dataValue.setLocalEventId(currentEvent.getLocalId());
        dataValue.setEvent(currentEvent.getEvent());
        dataValue.setProvidedElsewhere(false);
        if(Session.getUser()!=null)
            dataValue.setStoredBy(Session.getUser().getName());
        dataValue.setValue(value);
        dataValue.save();
    }

    @Override
    public void visit(CompositeScore compositeScore) {
        DataValue dataValue=new DataValue();
        dataValue.setDataElement(compositeScore.getUid());
        dataValue.setLocalEventId(currentEvent.getLocalId());
        dataValue.setEvent(currentEvent.getEvent());
        dataValue.setProvidedElsewhere(false);
        dataValue.setStoredBy(Session.getUser().getName());
        dataValue.setValue(Utils.round(ScoreRegister.getCompositeScore(compositeScore)));
        dataValue.save();
    }

    @Override
    public void visit(Value value) {
        DataValue dataValue=new DataValue();
        dataValue.setDataElement(value.getQuestion().getUid());
        dataValue.setLocalEventId(currentEvent.getLocalId());
        dataValue.setEvent(currentEvent.getEvent());
        dataValue.setProvidedElsewhere(false);
        dataValue.setStoredBy(Session.getUser().getName());
        //XXX In pictureapp always value since option.code is Khmer value
        dataValue.setValue(value.getValue());
        dataValue.save();
    }

    /**
     * Builds an event from a survey
     * @return
     */
    private Event buildEvent()throws Exception{
        currentEvent=new Event();

        currentEvent.setStatus(Event.STATUS_COMPLETED);
        currentEvent.setFromServer(false);
        currentEvent.setOrganisationUnitId(getSafeOrgUnitUID(currentSurvey));
        currentEvent.setProgramId(currentSurvey.getTabGroup().getProgram().getUid());
        currentEvent.setProgramStageId(currentSurvey.getTabGroup().getUid());
        updateEventLocation();
        updateEventDates();
        Log.d(TAG, "Saving event " + currentEvent.toString());
        currentEvent.save();
        return currentEvent;
    }

    private String getSafeOrgUnitUID(Survey survey){
        OrgUnit orgUnit=survey.getOrgUnit();
        if(orgUnit!=null){
            return orgUnit.getUid();
        }

        //A survey might be created with a 2.20 (no orgunit) but push into a 2.22
        return OrgUnit.findUIDByName(PreferencesState.getInstance().getOrgUnit());
    }

    /**
     * Fulfills the dates of the event
     */
    private void updateEventDates() {

        //Sent date 'now' (this change will be saves after successful push)
        //currentSurvey.setEventDate(new Date());

        //Creation date is null because it is used by sdk to POST|PUT we always POST a new survey
        currentEvent.setLastUpdated(EventExtended.format(currentSurvey.getCompletionDate()));
        currentEvent.setEventDate(EventExtended.format(currentSurvey.getEventDate()));
        currentEvent.setDueDate(EventExtended.format(currentSurvey.getScheduledDate()));
    }

    /**
     * Updates the location of the current event that it is being processed
     * @throws Exception
     */
    private void updateEventLocation() throws Exception{
        Location lastLocation = LocationMemory.get(currentSurvey.getId_survey());

        //No location + not required -> done
        if(lastLocation==null){
            return;
        }

        //location -> set lat/lng
        currentEvent.setLatitude(lastLocation.getLatitude());
        currentEvent.setLongitude(lastLocation.getLongitude());
    }

    /**
     * Annotates the survey and event that has been processed
     */
    private void annotateSurveyAndEvent() {
        surveys.add(currentSurvey);
        events.add(currentEvent);

        Log.d(TAG, String.format("%d surveys converted so far", surveys.size()));
    }

    /**
     * Saves changes in the survey (supposedly after a successfull push)
     * @param importSummaryMap
     */
    public void saveSurveyStatus(Map<Long,ImportSummary> importSummaryMap){
        for(int i=0;i<surveys.size();i++){
            Survey iSurvey=surveys.get(i);
            Event iEvent=events.get(i);
            ImportSummary importSummary=importSummaryMap.get(iEvent.getLocalId());
            FailedItem failedItem= hasConflict(iEvent.getLocalId());
            if(hasImportSummaryErrors(importSummary)){

                //Some error while pushing should be done again
                iSurvey.setStatus(Constants.SURVEY_COMPLETED);
                if(failedItem!=null) {
                    List<String> failedUids=getFailedUidQuestion(failedItem.getErrorMessage());
                    for(String uid:failedUids) {
                        Log.d(TAG, "PUSH process...Conflict in "+uid+" dataElement. Survey: "+iSurvey.getId_survey());
                        iSurvey.setStatus(Constants.SURVEY_CONFLICT);
                    }
                }
                iSurvey.save();

                //Generated event must be remove too
                iEvent.delete();
            }else{
                iSurvey.setStatus(Constants.SURVEY_SENT);
                iSurvey.saveMainScore();
                iSurvey.save();
                Log.d("DpBlank", "Saving suvey as completed " + iSurvey);
            }
        }
    }

    /**
     * Checks whether the given event contains errors in SDK FailedItem table or has been successful.
     * If not return null, it is becouse this item had a conflict.
     * @param localId
     * @return
     */
    private FailedItem hasConflict(long localId){
        return  new Select()
                .from(FailedItem.class)
                .where(Condition.column(FailedItem$Table.ITEMID)
                        .is(localId)).querySingle();
    }

    /**
     * Checks whether the given importSummary contains errors or has been successful.
     * An import with 0 importedItems is an error too.
     * @param importSummary
     * @return
     */
    private boolean hasImportSummaryErrors(ImportSummary importSummary){
        if(importSummary==null){
            return true;
        }

        if(importSummary.getImportCount()==null){
            return true;
        }
        return importSummary.getImportCount().getImported()==0;
    }


    /**
     * Get dataelement fails from errormessage JSON.
     * @param responseData
     * @return
     */
    private List<String> getFailedUidQuestion(String responseData){
        String message="";
        List<String> uid=new ArrayList<>();
        JSONArray jsonArrayResponse=null;
        JSONObject jsonObjectResponse= null;
        try {
            jsonObjectResponse = new JSONObject(responseData);
            message=jsonObjectResponse.getString("message");
            jsonObjectResponse=new JSONObject(jsonObjectResponse.getString("response"));
            jsonArrayResponse=new JSONArray(jsonObjectResponse.getString("importSummaries"));
            jsonObjectResponse=new JSONObject(jsonArrayResponse.getString(0));
            jsonArrayResponse=new JSONArray(jsonObjectResponse.getString("conflicts"));
            //values
            for(int i=0;i<jsonArrayResponse.length();i++) {
                jsonObjectResponse = new JSONObject(jsonArrayResponse.getString(i));
                uid.add(jsonObjectResponse.getString("object"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(message!="")
            ShowException.showError(message,PreferencesState.getInstance().getContext());
        return  uid;
    }
}
