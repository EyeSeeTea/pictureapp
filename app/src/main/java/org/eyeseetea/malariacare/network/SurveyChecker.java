package org.eyeseetea.malariacare.network;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.android.sdk.controllers.wrappers.EventsWrapper;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by idelcano on 03/10/2016.
 */

public class SurveyChecker {

    private static String TAG=".CheckSurveys";

    private static final String DHIS_CHECK_EVENT_API="/api/events.json??program=%s&orgUnit=%s&startDate=%s&endDate=%s&skipPaging=true&fields=event,orgUnit,dueDate,program,href,status,eventDate,orgUnitName,created,completedDate,lastUpdated,completedBy,dataValues";

    /**
     * Launch a new thread to checks all the quarantine surveys
     */
    public static void launchQuarantineChecker(){
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    int quarantineSurveysSize= Survey.countQuarantineSurveys();
                    Log.d(TAG,"Quarantine size: "+quarantineSurveysSize);
                    if(quarantineSurveysSize>1){
                        checkAllQuarantineSurveys();
                    }
                } finally {
                    Log.d(TAG,"Quarantine thread finished");
                }
            }
        };
        t.start();
    }

    /**
     * Get events filtered by program orgUnit and between dates.
     */
    public static List<Event> getEvents(String program, String orgUnit, Date minDate, Date maxDate) {
        try {
            Response response;

            String DHIS_URL = PreferencesState.getInstance().getDhisURL();
            String startDate = EventExtended.format(minDate,EventExtended.AMERICAN_DATE_FORMAT);
            String endDate = EventExtended.format(new Date(maxDate.getTime()+(8*24*60*60*1000)),EventExtended.AMERICAN_DATE_FORMAT);
            String url =String.format(DHIS_URL + DHIS_CHECK_EVENT_API,program,orgUnit,startDate,endDate);
            Log.d(TAG,url);
            url = ServerAPIController.encodeBlanks(url);

            response = ServerAPIController.executeCall(null, url, "GET");
            if (!response.isSuccessful()) {
                Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
            JSONObject events=new JSONObject(response.body().string());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.convertValue(mapper.readTree(events.toString()), JsonNode.class);

            return EventsWrapper.getEvents(jsonNode);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Download the related events. and checks all the quarantine surveys.
     * If a survey is in the server, the survey should be set as sent. Else, the survey should be set as completed and it will be resend.
     */
    public static void checkAllQuarantineSurveys() {
        List<Survey> quarantineSurveys=Survey.getAllQuarantineSurveys();
        Date minDate=Survey.getMinQuarantineEventDate();
        Date maxDate=Survey.getMaxQuarantineEventDate();
        String program = quarantineSurveys.get(0).getProgram().getUid();
        String orgUnit = OrgUnit.findUIDByName(PreferencesState.getInstance().getOrgUnit());
        List<Event> events= getEvents(program, orgUnit, minDate, maxDate);
        if(events!=null) {
            for (Survey survey : quarantineSurveys) {
                updateQuarantineSurveysStatus(events, survey);
            }
        }
    }

    /**
     * Given a list of events, check for the presence of that survey among the events, and update
     * consequently their status. If the survey exist (checked by completion date) then it's
     * considered as sent, otherwise it will be considered as just completed and awaiting to be sent
     * @param events
     * @param survey
     */
    private static void updateQuarantineSurveysStatus(List<Event> events, Survey survey) {
        boolean isSent=false;
        for (Event event : events) {
            isSent = surveyDateExistsInEventTimeCaptureControlDE(survey, event);
            if (isSent) {
                break;
            }
        }
        if(isSent){
            Log.d(TAG, "Set quarantine survey as sent" + survey.getId_survey());
            survey.setStatus(Constants.SURVEY_SENT);
        }
        else{
            //When the completion date for a survey is not present in the server, this survey is not in the server.
            //This survey is set as "completed" and will be send in the future.
            Log.d(TAG, "Set quarantine survey as completed" + survey.getId_survey());
            survey.setStatus(Constants.SURVEY_COMPLETED);
        }
        survey.save();
    }

    /**
     * Given an event, check through all its DVs if the survey completion date is present in the
     * event in the form of the control DE "Time Capture" whose UID is hardcoded
     * @param survey
     * @param event
     * @return
     */
    private static boolean surveyDateExistsInEventTimeCaptureControlDE(Survey survey, Event event) {
        for (DataValue dataValue : event.getDataValues()) {
            if (dataValue.getDataElement().equals(PushClient.DATETIME_CAPTURE_UID)
                    && dataValue.getValue().equals(EventExtended.format(survey.getCompletionDate(), EventExtended.COMPLETION_DATE_FORMAT))) {
                Log.d(TAG, "Found survey" + survey.getId_survey() + "date " + survey.getCompletionDate() + "dateevent" + dataValue.getValue());
                return true;
            }
        }
        return false;
    }
}
