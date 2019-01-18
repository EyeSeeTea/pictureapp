package org.eyeseetea.malariacare.data.sync.exporter;

import static junit.framework.Assert.assertTrue;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.data.database.datasources.CountryVersionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.rules.MockWebServerRule;
import org.eyeseetea.malariacare.utils.Constants;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WSPushControllerShould {

    @Rule
    public MockWebServerRule mockWebServerRule = new MockWebServerRule(new AssetsFileReader());

    @Mock
    ICredentialsRepository mCredentialsRepository;
    @Mock
    IDeviceRepository deviceDataSource;
    @Mock
    IAppInfoRepository appInfoDataSource;
    @Mock
    IProgramRepository programRepository;

    private static final String PUSH_RESPONSE_CONFLICT = "push_response_conflict.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";
    private static final String QUARANTINE_RESPONSE_CONFLICT = "quarantine_response_conflict.json";

    private String[] eventUIDs = {"LRR4ZZidQ6T", "PHp2WANFHE1", "NDqaWw51WJr", "Ian8YUgm7T3"};
    private List<Long> surveysIDs = new ArrayList<>();

    @Before
    public void setUp(){
        SurveyDB.deleteAll();
    }

    @Test
    public void push_callback_return_a_error_when_the_api_is_not_available()
            throws IOException {
        WSPushController mWSPushController = givenWSPushController();

        givenSomeTestSurveys();

        whenAPushHasNotAvailableApi();

        mWSPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onStartPushing() {
            }

            @Override
            public void onComplete() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onInformativeError(Throwable throwable) {
                fail("onSurveyNotFound");
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onSurveyNotFound");
            }

            @Override
            public void onError(Throwable throwable) {
                boolean hasError = throwable != null;
                assertThat(throwable, CoreMatchers.<Throwable>instanceOf(AvailableApiException.class));
                assertThat(hasError, is(true));
            }
        });
    }

    @Test
    public void put_survey_status_conflict_if_failed_or_no_success_action_status()
            throws IOException {
        WSPushController mWSPushController = givenWSPushController();
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
        WSPushController mWSPushController = givenWSPushController();
        givenSomeQuarantineTestSurveys();
        whenAQuarantineResponseWithSomeQuarantineSurveysIsReceived();
        assertTrue(SurveyDB.getAllQuarantineSurveys().size() == 4);
        mWSPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onStartPushing() {

            }

            @Override
            public void onComplete() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onInformativeError(Throwable throwable) {
                fail("onSurveyNotFound");
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onSurveyNotFound");
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

    private void whenAQuarantineResponseWithSomeQuarantineSurveysIsReceived() throws IOException {
        mockWebServerRule.getMockServer().enqueueMockResponse(QUARANTINE_RESPONSE_CONFLICT);
    }

    private void whenAPushResponseWithSomeConflictsIsReceived() throws IOException{
        mockWebServerRule.getMockServer().enqueueMockResponse(API_AVAILABLE_OK);
        mockWebServerRule.getMockServer().enqueueMockResponse(PUSH_RESPONSE_CONFLICT);
    }
    private void whenAPushHasNotAvailableApi() throws IOException{
        mockWebServerRule.getMockServer().enqueueMockResponse(API_AVAILABLE_NO_OK);
        mockWebServerRule.getMockServer().enqueueMockResponse(PUSH_RESPONSE_CONFLICT);
    }

    private void deleteTestConflictGeneratedValues() {
        for (Long surveyId : surveysIDs) {
            SurveyDB.findById(surveyId).delete();
        }
        ProgramDB.findByUID("testProgramId").delete();
    }

    public WSPushController givenWSPushController() {
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        ISettingsRepository settingsRepository = new SettingsDataSource(
                PreferencesState.getInstance().getContext());
        Date date = new Date();
        when(appInfoDataSource.getAppInfo()).thenReturn(new AppInfo("0", "0", "0", date, date));
        when(deviceDataSource.getDevice()).thenReturn(new Device("phoneNumber", "imei", "version"));
        when(mCredentialsRepository.getLastValidCredentials()).thenReturn(new Credentials(mockWebServerRule.getMockServer().getBaseEndpoint(), "test", "test"));
        when(programRepository.getUserProgram()).thenReturn(new Program("code", "uid"));
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(mockWebServerRule.getMockServer().getBaseEndpoint());
        ConvertToWSVisitor mConvertToWSVisitor = new ConvertToWSVisitor(deviceDataSource, mCredentialsRepository, settingsRepository, appInfoDataSource,
                programRepository, new CountryVersionLocalDataSource());
        return new WSPushController(mConvertToWSVisitor, eReferralsAPIClient, surveyRepository);
    }
}
