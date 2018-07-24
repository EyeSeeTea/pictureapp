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
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
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

public class PushUseCaseShould {

    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private CustomMockServer mCustomMockServer;
    private eReferralsAPIClient mEReferralsAPIClient;
    private WSPushController mWSPushController;
    private PushUseCase mPushUseCase;
    private int countSync;

    private Credentials previousCredentials;
    private Program previousProgram;
    private boolean previousPushInProgress;

    @Test
    public void setUserCanAddSurveysToFalseOn209Response() throws IOException, InterruptedException {
        final Object syncObject = new Object();
        countSync = 0;
        mCustomMockServer.enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
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

    @Before
    public void cleanUp() throws IOException {
        mCustomMockServer = new CustomMockServer(new AssetsFileReader());
        savePreviousPreferences();
        saveTestCredentialsAndProgram();
        mEReferralsAPIClient = new eReferralsAPIClient(mCustomMockServer.getBaseEndpoint());
        ConvertToWSVisitor convertToWSVisitor = new ConvertToWSVisitor(
                new Device("testPhone", "testIMEI", "test_version"),
                InstrumentationRegistry.getTargetContext());

        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        mWSPushController = new WSPushController(mEReferralsAPIClient, surveyRepository, convertToWSVisitor);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);
        mPushUseCase = new PushUseCase(mWSPushController, asyncExecutor, mainExecutor,
                surveysThresholds, surveyRepository, orgUnitRepository);
    }


    @After
    public void tearDown() throws IOException {
        mCustomMockServer.shutdown();
        restorePreferences();
    }


    private void savePreviousPreferences() {
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        previousCredentials = credentialsLocalDataSource.getOrganisationCredentials();
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        ProgramDB databaseProgramDB =
                ProgramDB.getProgram(
                        PreferencesEReferral.getUserProgramId());
        if (databaseProgramDB != null) {
            previousProgram = programLocalDataSource.getUserProgram();
        }
        previousPushInProgress = PreferencesState.getInstance().isPushInProgress();
    }

    private void saveTestCredentialsAndProgram() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.dhis_url), "test");
        editor.commit();

        Credentials credentials = new Credentials("test", "test", "test");
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveOrganisationCredentials(credentials);
        ProgramDB programDB = new ProgramDB("testProgramId", "testProgram");
        programDB.save();
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        programLocalDataSource.saveUserProgramId(new Program("testProgram", "testProgramId"));
        PreferencesState.getInstance().setPushInProgress(false);
        UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
        userAccountDataSource.saveLoggedUser(
                new UserAccount("testUsername", "testUserUID", false, true));
    }

    private void restorePreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        if (previousCredentials != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.dhis_url),
                    previousCredentials.getServerURL());
            editor.commit();
        }
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveOrganisationCredentials(previousCredentials);
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        if (previousProgram != null) {
            programLocalDataSource.saveUserProgramId(previousProgram);
        } else {
            PreferencesEReferral.saveUserProgramId(-1l);
        }
        PreferencesState.getInstance().setPushInProgress(previousPushInProgress);
    }

}
