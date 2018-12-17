package org.eyeseetea.malariacare.data.sync.exporter;

import static junit.framework.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
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
    private static final String QUARANTINE_RESPONSE_CONFLICT = "quarantine_response_conflict.json";

    private MockWebServer server;
    private eReferralsAPIClient apiClient;
    private WSPushController mWSPushController;
    private String[] eventUIDs = {"LRR4ZZidQ6T", "PHp2WANFHE1", "NDqaWw51WJr", "Ian8YUgm7T3"};
    private List<Long> surveysIDs = new ArrayList<>();

    private String serverPreference="";
    private String userPreference ="";
    private String pinPreference ="";
    private long updatedTimePreference = 0l;
    private long programPreference =-1;

    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        this.server = new MockWebServer();
        this.server.start();
        //PreferencesState.getInstance().setContext(InstrumentationRegistry.getInstrumentation().getTargetContext());
        savePreferences();
        saveTestCredentialsAndProgram();
        apiClient = initializeApiClient();
        Device device = new Device("phoneNumber", "imei", "version");

        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        ConvertToWSVisitor convertToWSVisitor = new ConvertToWSVisitor(device, mContext);
        mWSPushController = new WSPushController(apiClient, surveyRepository,
                convertToWSVisitor);
    }

    private void savePreferences() {
        Context context = PreferencesState.getInstance().getContext();
        serverPreference = (PreferenceManager.getDefaultSharedPreferences(
                context)).getString(context.getString(R.string.web_service_url), context.getString(R.string.ws_base_url));
        userPreference = (PreferenceManager.getDefaultSharedPreferences(
                context)).getString(context.getString(R.string.logged_user_username),"");
        pinPreference = (PreferenceManager.getDefaultSharedPreferences(
                context)).getString(context.getString(R.string.logged_user_pin),"");
        programPreference = (PreferenceManager.getDefaultSharedPreferences(
                context)).getLong(context.getString(R.string.logged_user_program),-1);
        AppInfoDataSource appInfoDataSource = new AppInfoDataSource(mContext);
        AppInfo appInfo = appInfoDataSource.getAppInfo();
        if(appInfo.getUpdateMetadataDate() != null)
        {
            updatedTimePreference = appInfo.getUpdateMetadataDate().getTime();
        }
    }

    private void restorePreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url), serverPreference);
        editor.putString(context.getString(R.string.logged_user_username), userPreference);
        editor.putString(context.getString(R.string.logged_user_pin), pinPreference);
        editor.putLong(context.getString(R.string.logged_user_program), programPreference);
        editor.putLong(context.getString(R.string.metadata_update_date), updatedTimePreference);
        editor.commit();
    }


    private void saveTestCredentialsAndProgram() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url),
                context.getString(R.string.ws_base_url));
        editor.putLong(context.getString(R.string.metadata_update_date), 1545047483);

        editor.commit();

        Credentials credentials = new Credentials(context.getString(R.string.ws_base_url), "test",
                "test");
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveLastValidCredentials(credentials);
        ProgramDB programDB = new ProgramDB("testProgramId", "testProgram");
        programDB.save();
        ProgramRepository programRepository = new ProgramRepository();
        programRepository.saveUserProgramId(new Program("testProgram", "testProgramId"));
    }

    @Test
    public void put_survey_status_conflict_if_failed_or_no_success_action_status()
            throws IOException {
        givenSomeTestSurveys();

        whenAPushResponseWithSomeConflictsIsReceived();

        mWSPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onStartPushing() {

            }

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
                deleteTestConflictGeneratedValues();
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
                deleteTestConflictGeneratedValues();
                boolean hasError = throwable != null;
                assertThat(hasError, is(false));
            }
        });
    }

    @Test
    public void update_quarantine_with_correct_status_when_do_push_with_some_quarantine_surveys()
            throws IOException {
        givenSomeQuarantineTestSurveys();
        whenAQuarantineResponseWithSomeQuarantineSurveysIsReceived();
        assertTrue(SurveyDB.getAllQuarantineSurveys().size() == 4);
        mWSPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onStartPushing() {

            }

            @Override
            public void onComplete() {
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
                assertThat(hasError, is(true));
                assertTrue(SurveyDB.getAllQuarantineSurveys().size() == 0);
                assertTrue(SurveyDB.findByUid("LRR4ZZidQ6T").getStatus()
                        == Constants.SURVEY_COMPLETED);
                assertTrue(SurveyDB.findByUid("PHp2WANFHE1").getStatus() == Constants.SURVEY_SENT);
                assertTrue(SurveyDB.findByUid("NDqaWw51WJr").getStatus() == Constants.SURVEY_SENT);
                assertTrue(SurveyDB.findByUid("Ian8YUgm7T3").getStatus() == Constants.SURVEY_SENT);
            }
        });
    }

    private void givenSomeQuarantineTestSurveys() {
        ProgramDB programDB = new ProgramDB("test", "uid");
        programDB.save();
        OrgUnitDB orgUnitDB = new OrgUnitDB("test");
        orgUnitDB.save();
        for (String eventUID : eventUIDs) {
            SurveyDB surveyDB = new SurveyDB(orgUnitDB, programDB,
                    new UserDB("test", "test"));
            surveyDB.setStatus(Constants.SURVEY_QUARANTINE);
            surveyDB.setEventUid(eventUID);
            surveyDB.save();
        }
    }

    private void whenAQuarantineResponseWithSomeQuarantineSurveysIsReceived() throws IOException {
        enqueueResponse(QUARANTINE_RESPONSE_CONFLICT);
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

    private void deleteTestConflictGeneratedValues() {
        for (Long surveyId : surveysIDs) {
            SurveyDB.findById(surveyId).delete();
        }
        ProgramDB.findByUID("testProgramId").delete();
    }


    @After
    public void tearDown() throws IOException {
        server.shutdown();
        restorePreferences();
    }


    private eReferralsAPIClient initializeApiClient() {
        return new eReferralsAPIClient(server.url("/").toString());
    }

    private void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        String fileContent = new AssetsFileReader().getStringFromFile(fileName);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }
}
