package org.eyeseetea.malariacare.services;


import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.ConvertToWSVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PushServiceShould {
    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private CustomMockServer mCustomMockServer;
    private eReferralsAPIClient mEReferralsAPIClient;
    private WSPushController mWSPushController;
    private PushUseCase mPushUseCase;
    private PushServiceStrategy mPushServiceStrategy;
    private boolean showLogiIntentReceived;
    private final Object syncObject = new Object();

    private Credentials previousCredentials;
    private Program previousProgram;
    private boolean previousPushInProgress;
    private UserAccount previousUserAccount;

    private Context mContext;

    @Test
    public void launchLoginIntentOn209APIResponse() throws IOException, InterruptedException {
        showLogiIntentReceived = false;
        mCustomMockServer.enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        mPushServiceStrategy.executePush(mPushUseCase);
        Log.d("Executing push service strategy test", "testing 209");
        synchronized (syncObject) {
            syncObject.wait();
        }
        assertThat(showLogiIntentReceived, is(true));
    }

    @Before
    public void cleanUp() throws IOException {
        mContext = InstrumentationRegistry.getTargetContext();
        grantPhonePermission();
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
        mCustomMockServer = new CustomMockServer(new AssetsFileReader());
        savePreviousPreferences();
        saveTestCredentialsAndProgram();
        mEReferralsAPIClient = new eReferralsAPIClient(mCustomMockServer.getBaseEndpoint());
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        Device device = new Device("test", "test", "test");
        ConvertToWSVisitor convertToWSVisitor = new ConvertToWSVisitor(device, mContext);
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
        mPushServiceStrategy = new PushServiceStrategy(new PushService("TestPushService"));
    }


    @After
    public void tearDown() throws IOException {
        mCustomMockServer.shutdown();
        restorePreferences();
    }

    public void grantPhonePermission() {
        // In M+, trying to call a number will trigger a runtime dialog. Make sure
        // the permission is granted before running this test.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + mContext.getPackageName()
                            + " android.permission.READ_PHONE_STATE");
        }
    }


    private void savePreviousPreferences() {
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        previousCredentials = credentialsLocalDataSource.getLastValidCredentials();
        ProgramRepository programRepository = new ProgramRepository();
        ProgramDB databaseProgramDB =
                ProgramDB.getProgram(
                        PreferencesEReferral.getUserProgramId());
        if (databaseProgramDB != null) {
            previousProgram = programRepository.getUserProgram();
        }
        previousPushInProgress = PreferencesState.getInstance().isPushInProgress();
        UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
        previousUserAccount = userAccountDataSource.getLoggedUser();
    }

    private void saveTestCredentialsAndProgram() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url),
                context.getString(R.string.ws_base_url));
        editor.commit();

        Credentials credentials = new Credentials(context.getString(R.string.ws_base_url), "test", "test");
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveLastValidCredentials(credentials);
        ProgramDB programDB = new ProgramDB("testProgramId", "testProgram");
        programDB.save();
        ProgramRepository programRepository = new ProgramRepository();
        programRepository.saveUserProgramId(new Program("testProgram", "testProgramId"));
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
            editor.putString(context.getString(R.string.web_service_url),
                    previousCredentials.getServerURL());
            editor.commit();
        }
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveLastValidCredentials(previousCredentials);
        ProgramRepository programRepository = new ProgramRepository();
        if (previousProgram != null) {
            programRepository.saveUserProgramId(previousProgram);
        } else {
            PreferencesEReferral.saveUserProgramId(-1l);
        }
        PreferencesState.getInstance().setPushInProgress(previousPushInProgress);
        if (previousUserAccount != null) {
            UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
            userAccountDataSource.saveLoggedUser(previousUserAccount);
        }
    }


    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginIfConfigFileObsolete(intent);
        }

        private void showLoginIfConfigFileObsolete(Intent intent) {
            if (intent.getBooleanExtra(PushServiceStrategy.SHOW_LOGIN, false)) {
                synchronized (syncObject) {
                    showLogiIntentReceived = true;
                    syncObject.notify();
                }
            }
        }
    };


}
