package org.eyeseetea.malariacare.network;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.strategies.SurveyCheckerStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SurveyChecker {
    private static String TAG = ".CheckSurveys";

    /**
     * Launch a new thread to checks all the quarantine surveys
     */
    public static void launchQuarantineChecker() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    int quarantineSurveysSize = SurveyDB.countQuarantineSurveys();
                    Log.d(TAG, "Quarantine size: " + quarantineSurveysSize);
                    if (quarantineSurveysSize > 0) {
                        checkAllQuarantineSurveys();
                        PushService.reloadDashboard();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "Quarantine thread finished");
                }
            }
        };
        t.start();
    }

    /**
     * Get events filtered by program orgUnit and between dates.
     */
    public static List<EventExtended> getEvents(String program, String orgUnit, Date minDate,
            Date maxDate) throws ApiCallException {
            Response response;

            String DHIS_URL = PreferencesState.getInstance().getDhisURL();
            String startDate = EventExtended.format(minDate, EventExtended.AMERICAN_DATE_FORMAT);
            String endDate = EventExtended.format(
                    new Date(maxDate.getTime() + (8 * 24 * 60 * 60 * 1000)),
                    EventExtended.AMERICAN_DATE_FORMAT);
            String url = SurveyCheckerStrategy.getApiEventUrl(DHIS_URL, program, orgUnit, startDate,
                    endDate);
            Log.d(TAG, url);
            url = ServerApiUtils.encodeBlanks(url);

            response = ServerApiCallExecution.executeCall(null, url, "GET");
            JSONObject events = null;
            try {
                events = new JSONObject(ServerApiUtils.getReadableBodyResponse(response));
            } catch (JSONException ex) {
                throw new ApiCallException(ex);
            }
            JsonNode jsonNode = ServerApiUtils.getJsonNodeMappedResponse(events);

            return getEvents(jsonNode);
    }

    /**
     * Download the related events. and checks all the quarantine surveys.
     * If a survey is in the server, the survey should be set as sent. Else, the survey should be
     * set as completed and it will be resend.
     */
    public static void checkAllQuarantineSurveys(){
        List<ProgramDB> programDBs = ProgramDB.getAllPrograms();
        for (ProgramDB programDB : programDBs) {
            for (OrgUnitDB orgUnitDB : SurveyDB.getQuarantineOrgUnits(programDB.getId_program())) {
                List<SurveyDB> quarantineSurveyDBs = SurveyDB.getAllQuarantineSurveysByProgramAndOrgUnit(
                        programDB, orgUnitDB);
                if (quarantineSurveyDBs.size() == 0) {
                    continue;
                }
                Date minDate = SurveyDB.getMinQuarantineCompletionDateByProgramAndOrgUnit(programDB,
                        orgUnitDB);
                Date maxDate = SurveyDB.getMaxQuarantineEventDateByProgramAndOrgUnit(programDB,
                        orgUnitDB);
                List<EventExtended> events;
                try {
                    events = getEvents(programDB.getUid(), orgUnitDB.getUid(),
                            minDate,
                            maxDate);
                }catch (ApiCallException e){
                    e.printStackTrace();
                    return;
                }
                if(events==null){
                    return;
                }
                for (SurveyDB surveyDB : quarantineSurveyDBs) {
                    if (events.size() > 0) {
                        updateQuarantineSurveysStatus(events, surveyDB);
                    } else {
                        changeSurveyStatusFromQuarantineTo(surveyDB, Constants.SURVEY_COMPLETED);
                    }
                }

            }
        }
    }

    public static List<EventExtended> getEvents(JsonNode jsonNode) {
        TypeReference<List<Event>> typeRef =
                new TypeReference<List<Event>>() {
                };
        List<Event> events;
        try {
            if (jsonNode.has("events")) {
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());
                events = objectMapper.
                        readValue(jsonNode.get("events").traverse(), typeRef);
            } else {
                events = new ArrayList<>();
            }
        } catch (IOException e) {
            events = new ArrayList<>();
            e.printStackTrace();
        }
        List<EventExtended> eventExtendedList = new ArrayList<>();
        for (Event event : events) {
            EventFlow eventFlow = EventFlow.MAPPER.mapToDatabaseEntity(event);
            EventExtended eventExtended = new EventExtended(eventFlow);
            if (event.getDataValues() != null && event.getDataValues().size() > 0) {
                List<TrackedEntityDataValueFlow> trackedEntityDataValueFlows =
                        TrackedEntityDataValueFlow.MAPPER.mapToDatabaseEntities(
                                event.getDataValues());
                eventExtended.setDataValuesInMemory(trackedEntityDataValueFlows);
            }
            eventExtendedList.add(eventExtended);
        }
        return eventExtendedList;
    }

    public static void updateQuarantineSurveysStatus(List<EventExtended> events, SurveyDB surveyDB) {
        boolean isSent = false;
        for (EventExtended event : events) {
            isSent = surveyDateExistsInEventTimeCaptureControlDE(surveyDB, event);
            if (isSent) {
                break;
            }
        }
        //When the completion date for a survey is not present in the server, this survey is
        // not in the server.
        //This survey is set as "completed" and will be send in the future.
        changeSurveyStatusFromQuarantineTo(surveyDB, (isSent) ? Constants.SURVEY_SENT : Constants.SURVEY_COMPLETED);

    }

    private static void changeSurveyStatusFromQuarantineTo(SurveyDB surveyDB, int status){
        try {
            Log.d(TAG, "Set quarantine survey as " + ((status == Constants.SURVEY_SENT) ? "sent "
                    : "complete ") + surveyDB.getId_survey() + " date "
                    + EventExtended.format(surveyDB.getCreationDate(),
                    EventExtended.DHIS2_GMT_DATE_FORMAT));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if(surveyDB.isQuarantine()){
            surveyDB.setStatus(status);
            surveyDB.save();
        }
    }

    /**
     * Given an event, check through all its DVs if the survey completion date is present in the
     * event in the form of the control DE "Time Capture" whose UID is hardcoded
     */
    private static boolean surveyDateExistsInEventTimeCaptureControlDE(SurveyDB surveyDB,
            EventExtended event) {
        for (DataValueExtended dataValue : DataValueExtended.getExtendedList(
                event.getDataValuesInMemory())) {
            if (dataValue.getDataElement().equals(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.control_data_element_datetime_capture))
                    && dataValue.getValue().equals(EventExtended.format(surveyDB.getCompletionDate(),
                    EventExtended.DHIS2_GMT_DATE_FORMAT))) {
                Log.d(TAG, "Found survey" + surveyDB.getId_survey() + "date "
                        + surveyDB.getCreationDate() + "dateevent" + dataValue.getValue());
                return true;
            }
        }
        return false;
    }
}
