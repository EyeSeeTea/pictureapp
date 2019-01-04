package org.eyeseetea.malariacare.data.sync.exporter;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.CommonTestPreferencesControl;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class PushUseCaseShould extends CommonTestPreferencesControl {

    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";
    private CustomMockServer mCustomMockServer;
    private eReferralsAPIClient mEReferralsAPIClient;
    private WSPushController mWSPushController;
    private PushUseCase mPushUseCase;
    private int countSync;

    private Context mContext;

    @Before
    public void cleanUp() throws IOException {
        mContext = InstrumentationRegistry.getTargetContext();
        mCustomMockServer = new CustomMockServer(new AssetsFileReader());
        savePreviousPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.web_service_url),
               mCustomMockServer.getBaseEndpoint());
        editor.commit();
        saveTestCredentialsAndProgram();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        ConvertToWSVisitor convertToWSVisitor = new ConvertToWSVisitor(
                new Device("testPhone", "testIMEI", "test_version"),
                mContext);
        mEReferralsAPIClient = new eReferralsAPIClient(mCustomMockServer.getBaseEndpoint());
        mWSPushController = new WSPushController(mEReferralsAPIClient, surveyRepository,
                convertToWSVisitor);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);
        mPushUseCase = new PushUseCase(mWSPushController, asyncExecutor, mainExecutor,
                surveysThresholds, surveyRepository, orgUnitRepository);
    }


    @Test
    public void call_on_push_error_when_ape_is_not_available_during_push() throws IOException, InterruptedException {
        final Object syncObject = new Object();
        countSync = 0;
        mCustomMockServer.enqueueMockResponseFileName(200, API_AVAILABLE_NO_OK);
        mCustomMockServer.enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setVoucherUid("1323544116");
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        mPushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {
            }

            @Override
            public void onComplete() {
                fail("onComplete");
            }

            @Override
            public void onPushError() {
                synchronized (syncObject) {
                    if (countSync == 0) {
                        syncObject.notify();
                    }
                    countSync++;
                }
            }

            @Override
            public void onPushInProgressError() {
                fail("onPushInProgressError");
            }

            @Override
            public void onSurveysNotFoundError() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onConversionError() {
                fail("onConversionError");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onInformativeError(String message) {
                fail("onInformativeMessage: " + message);
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onInformativeMessage" + message);
            }

            @Override
            public void onClosedUser() {
                fail("onClosedUser");
            }

            @Override
            public void onBannedOrgUnit() {
                fail("onBannedOrgUnit");
            }

            @Override
            public void onReOpenOrgUnit() {
                fail("onReOpenOrgUnit");
            }

            @Override
            public void onApiCallError() {
                fail("onApiCallError");
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                fail("onApiCallError");
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }

    }

    @Test
    public void setUserCanAddSurveysToFalseOn209Response() throws IOException, InterruptedException {
        final Object syncObject = new Object();
        countSync = 0;
        mCustomMockServer.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mCustomMockServer.enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setVoucherUid("1323544116");
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        mPushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {

            }

            @Override
            public void onComplete() {
                List<SurveyDB> surveys = SurveyDB.getAllSurveys();
                assertThat(surveys.get(0).getStatus(), is(Constants.SURVEY_SENT));
                surveyDB.delete();
                synchronized (syncObject) {
                    if (countSync == 1) {
                        syncObject.notify();
                    }
                    countSync++;
                }
            }

            @Override
            public void onPushError() {
                fail("onPushError");
            }

            @Override
            public void onPushInProgressError() {
                fail("onPushInProgressError");
            }

            @Override
            public void onSurveysNotFoundError() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onConversionError() {
                fail("onConversionError");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onInformativeError(String message) {
                fail("onInformativeError: " + message);
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onInformativeMessage: " + message);
            }

            @Override
            public void onClosedUser() {
                fail("onClosedUser");
            }

            @Override
            public void onBannedOrgUnit() {
                fail("onBannedOrgUnit");
            }

            @Override
            public void onReOpenOrgUnit() {
                fail("onReOpenOrgUnit");
            }

            @Override
            public void onApiCallError() {
                fail("onApiCallError");
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
                assertThat(userAccountDataSource.getLoggedUser().canAddSurveys(), is(false));
                synchronized (syncObject) {
                    if (countSync == 1) {
                        syncObject.notify();
                    }
                    countSync++;
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }

    }

    @After
    public void tearDown() throws IOException {
        mCustomMockServer.shutdown();
        restorePreferences();
    }

}
