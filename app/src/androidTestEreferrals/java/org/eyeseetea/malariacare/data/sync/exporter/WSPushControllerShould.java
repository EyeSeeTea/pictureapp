package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.test.utils.AssetsFileReader;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class WSPushControllerShould {

    private static final String PUSH_RESPONSE_CONFLICT = "push_response_conflict.json";

    private MockWebServer server;
    private eReferralsAPIClient apiClient;
    private WSPushController mWSPushController;
    private String[] eventUIDs = {"LRR4ZZidQ6T", "PHp2WANFHE1", "NDqaWw51WJr", "Ian8YUgm7T3"};
    private List<Long> surveysIDs = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        this.server = new MockWebServer();
        this.server.start();
        apiClient = initializeApiClient();
        mWSPushController = new WSPushController(apiClient);
    }


    @Test
    public void put_survey_status_conflict_if_failed_or_no_success_action_status()
            throws IOException {
        givenSomeTestSurveys();

        whenAPushResponseWithSomeConflictsIsReceived();

        mWSPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onComplete() {
                SurveyDB firstNoConflict = SurveyDB.findById(surveysIDs.get(0));
                assertThat(firstNoConflict.getStatus(), is(Constants.SURVEY_SENT));
                SurveyDB firstConflict = SurveyDB.findById(surveysIDs.get(1));
                assertThat(firstConflict.getStatus(), is(Constants.SURVEY_CONFLICT));
                SurveyDB secondConflict = SurveyDB.findById(surveysIDs.get(2));
                assertThat(secondConflict.getStatus(), is(Constants.SURVEY_CONFLICT));
                SurveyDB secondNoConflict = SurveyDB.findById(surveysIDs.get(3));
                assertThat(secondNoConflict.getStatus(), is(Constants.SURVEY_SENT));
            }

            @Override
            public void onInformativeError(Throwable throwable) {
                boolean hasError = throwable != null;
                assertThat(hasError, is(true));
            }

            @Override
            public void onInformativeMessage(String message) {
            }

            @Override
            public void onError(Throwable throwable) {
                boolean hasError = throwable != null;
                assertThat(hasError, is(false));
            }
        });
    }

    private void givenSomeTestSurveys() {
        for (String eventUID : eventUIDs) {
            SurveyDB surveyDB = new SurveyDB(new OrgUnitDB("test"), new ProgramDB("test"),
                    new UserDB("test", "test"));
            surveyDB.setStatus(Constants.SURVEY_COMPLETED);
            surveyDB.setEventUid(eventUID);
            surveyDB.save();
            surveysIDs.add(surveyDB.getId_survey());
        }
    }

    private void whenAPushResponseWithSomeConflictsIsReceived() throws IOException{
        enqueueResponse(PUSH_RESPONSE_CONFLICT);
    }


    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }


    private eReferralsAPIClient initializeApiClient() {
        return new eReferralsAPIClient(server.url("/").toString());
    }

    private void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        String fileContent = new AssetsFileReader().getStringFromFile(fileName,testContext);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }
}
