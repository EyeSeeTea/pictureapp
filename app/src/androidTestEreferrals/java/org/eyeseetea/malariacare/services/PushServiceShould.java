package org.eyeseetea.malariacare.services;


import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.database.datasources.CountryVersionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.exporter.ConvertToWSVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.rules.MockWebServerRule;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class PushServiceShould {
    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";

    private PushServiceStrategy mPushServiceStrategy;
    private boolean pullRequiredIntentReceived;
    private final Object syncObject = new Object();

    @Rule
    public MockWebServerRule mockWebServerRule = new MockWebServerRule(new AssetsFileReader());
    @Mock
    IDeviceRepository deviceDataSource;
    @Mock
    IAppInfoRepository appInfoDataSource;
    @Mock
    ICredentialsRepository mCredentialsRepository;
    @Mock
    ISettingsRepository mSettingsRepository;
    @Mock
    IProgramRepository mProgramRepository;

    private Context mContext;

    @Test
    public void launchLoginIntentOn209APIResponse() throws IOException, InterruptedException {
        PushUseCase pushUseCase = givenPushUseCase();
        pullRequiredIntentReceived = false;
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(209, API_AVAILABLE_OK);
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        mPushServiceStrategy.executePush(pushUseCase);
        Log.d("Executing push service strategy test", "testing 209");
        synchronized (syncObject) {
            syncObject.wait();
        }
        assertThat(pullRequiredIntentReceived, is(true));
    }

    @Before
    public void cleanUp() throws IOException {
        mContext = InstrumentationRegistry.getTargetContext();
        grantPhonePermission();
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
        mPushServiceStrategy = new PushServiceStrategy(new PushService("TestPushService"));
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

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginIfConfigFileObsolete(intent);
        }
        private void showLoginIfConfigFileObsolete(Intent intent) {
            if (intent.getBooleanExtra(PushServiceStrategy.PULL_REQUIRED, false)) {
                synchronized (syncObject) {
                    pullRequiredIntentReceived = true;
                    syncObject.notify();
                }
            }
        }
    };

    private WSPushController givenWSPushController() {
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        Date date = new Date();
        when(appInfoDataSource.getAppInfo()).thenReturn(new AppInfo("0", "0", "0", date, date));
        when(deviceDataSource.getDevice()).thenReturn(new Device("phoneNumber", "imei", "version"));

        when(mCredentialsRepository.getLastValidCredentials()).thenReturn(new Credentials(mockWebServerRule.getMockServer().getBaseEndpoint(), "test", "test"));
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(mockWebServerRule.getMockServer().getBaseEndpoint());
        when(mProgramRepository.getUserProgram()).thenReturn(new Program("code","testProgramId"));
        Settings settings = new Settings("en", "en", null, false, false, false,
                "test", "test", mockWebServerRule.getMockServer().getBaseEndpoint(), null, null,
                null, null, false, false, "1.0");
        when(mSettingsRepository.getSettings()).thenReturn(settings);
        ConvertToWSVisitor mConvertToWSVisitor = new ConvertToWSVisitor(deviceDataSource, mCredentialsRepository, mSettingsRepository, appInfoDataSource,
                mProgramRepository, new CountryVersionLocalDataSource());
        return new WSPushController(mConvertToWSVisitor, eReferralsAPIClient, surveyRepository);
    }

    private PushUseCase givenPushUseCase() {

        WSPushController mWSPushController = givenWSPushController();

        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IAsyncExecutor asyncExecutor = new IAsyncExecutor() {
            @Override
            public void run(Runnable runnable) {
                runnable.run();
            }
        };
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);
        return new PushUseCase(mWSPushController, asyncExecutor, mainExecutor,
                surveysThresholds, surveyRepository, orgUnitRepository);
    }

}
