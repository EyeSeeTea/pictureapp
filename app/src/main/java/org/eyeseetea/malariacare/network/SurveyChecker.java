package org.eyeseetea.malariacare.network;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.strategies.SurveyCheckerStrategy;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SurveyChecker {

    private static String mCategoryOptionUID;

    private static final String DHIS_CHECK_EVENT_API =
            "/api/events.json?program=%s&orgUnit=%s&startDate=%s&endDate=%s&attributeCos=%s"
                    + "&attributeCc=$s&skipPaging=true"
                    + "&fields=event,orgUnit,program,dataValues";
    private static String TAG = ".CheckSurveys";

    /**
     * Launch a new thread to checks all the quarantine surveys
     */
    public static void launchQuarantineChecker() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    int quarantineSurveysSize = Survey.countQuarantineSurveys();
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
            Date maxDate) {
        try {
            Response response;

            String DHIS_URL = PreferencesState.getInstance().getDhisURL();
            String startDate = EventExtended.format(minDate, EventExtended.AMERICAN_DATE_FORMAT);
            String endDate = EventExtended.format(
                    new Date(maxDate.getTime() + (8 * 24 * 60 * 60 * 1000)),
                    EventExtended.AMERICAN_DATE_FORMAT);
            String url = String.format(DHIS_URL + DHIS_CHECK_EVENT_API, program, orgUnit, startDate,
                    endDate, getCategoryOptionUIDByCurrentUser(),
                    PreferencesState.getInstance().getContext().getString(
                            R.string.category_combination));
            Log.d(TAG, url);
            url = ServerAPIController.encodeBlanks(url);

            response = ServerAPIController.executeCall(null, url, "GET");
            if (!response.isSuccessful()) {
                Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
            JSONObject events = new JSONObject(response.body().string());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.convertValue(mapper.readTree(events.toString()),
                    JsonNode.class);

            return getEvents(jsonNode);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Download the related events. and checks all the quarantine surveys.
     * If a survey is in the server, the survey should be set as sent. Else, the survey should be
     * set as completed and it will be resend.
     */
    public static void checkAllQuarantineSurveys() {
        List<Program> programs = Program.getAllPrograms();
        for (Program program : programs) {
            for (OrgUnit orgUnit : Survey.getQuarantineOrgUnits(program.getId_program())) {
                List<Survey> quarantineSurveys = Survey.getAllQuarantineSurveysByProgramAndOrgUnit(
                        program, orgUnit);
                if (quarantineSurveys.size() == 0) {
                    continue;
                }
                Date minDate = Survey.getMinQuarantineCompletionDateByProgramAndOrgUnit(program,
                        orgUnit);
                Date maxDate = Survey.getMaxQuarantineEventDateByProgramAndOrgUnit(program,
                        orgUnit);
                List<EventExtended> events = getEvents(program.getUid(), orgUnit.getUid(), minDate,
                        maxDate);
                if (events != null && events.size() > 0) {
                    for (Survey survey : quarantineSurveys) {
                        updateQuarantineSurveysStatus(events, survey);
                    }
                }

            }
        }
    }

    /**
     * Given a list of events, check for the presence of that survey among the events, and update
     * consequently their status. If the survey exist (checked by completion date) then it's
     * considered as sent, otherwise it will be considered as just completed and awaiting to be
     * sent
     */
    private static void updateQuarantineSurveysStatus(List<EventExtended> events, Survey survey) {
        SurveyCheckerStrategy surveyCheckerStrategy = new SurveyCheckerStrategy();
        surveyCheckerStrategy.updateQuarantineSurveysStatus(events, survey);
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

    private static String getCategoryOptionUIDByCurrentUser() {
        if (mCategoryOptionUID == null) {
            mCategoryOptionUID = SdkQueries.getCategoryOptionUIDByCurrentUser();
        }

        return mCategoryOptionUID;
    }

}
