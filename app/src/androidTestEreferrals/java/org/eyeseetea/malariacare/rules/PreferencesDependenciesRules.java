package org.eyeseetea.malariacare.rules;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.CountryVersionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.AuthenticationWSDataSource;
import org.eyeseetea.malariacare.data.remote.IForgotPasswordDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.ConvertToWSVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Date;

import static org.mockito.Mockito.when;

public class PreferencesDependenciesRules implements TestRule {


    Credentials credentials;

    private CustomMockServer mCustomMockServer;

    private void before() throws IOException {
        mCustomMockServer = new CustomMockServer(new AssetsFileReader());
        credentials = new Credentials(mCustomMockServer.getBaseEndpoint(), "test", "test");
    }

    private void after() throws IOException {
        mCustomMockServer.shutdown();
    }

    public CustomMockServer getMockServer() {
        return mCustomMockServer;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public LoginUseCase givenALoginUseCase(IConnectivityManager connectivityManager, IAuthenticationDataSource authenticationLocalDataSource,
                                           IForgotPasswordDataSource forgotPasswordDataSource, ISettingsRepository settingsRepository, ICredentialsRepository credentialsRepository,
                                           IInvalidLoginAttemptsRepository iInvalidLoginAttemptsRepository) {
        when(connectivityManager.isDeviceOnline()).thenReturn(true);
        IAuthenticationDataSource authenticationDataSource = new AuthenticationWSDataSource(new eReferralsAPIClient(mCustomMockServer.getBaseEndpoint()));

        AuthenticationManager authenticationManager = new AuthenticationManager(authenticationLocalDataSource, authenticationDataSource, new UserAccountDataSource(),
                forgotPasswordDataSource, settingsRepository);

        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new IAsyncExecutor() {
            @Override
            public void run(Runnable runnable) {
                runnable.run();
            }
        };

        when(credentialsRepository.getLastValidCredentials()).thenReturn(credentials);

        when(iInvalidLoginAttemptsRepository.getInvalidLoginAttempts()).thenReturn(new InvalidLoginAttempts(0,30));
        return new LoginUseCase(connectivityManager, authenticationManager, mainExecutor, asyncExecutor, credentialsRepository, iInvalidLoginAttemptsRepository);
    }

    public WSPushController givenWSPushController(IAppInfoRepository appInfoDataSource, IDeviceRepository deviceDataSource,
                                                  ICredentialsRepository mCredentialsRepository, IProgramRepository programRepository) {
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        ISettingsRepository settingsRepository = new SettingsDataSource(
                PreferencesState.getInstance().getContext());
        Date date = new Date();
        when(appInfoDataSource.getAppInfo()).thenReturn(new AppInfo("0", "0", "0", date, date));
        when(deviceDataSource.getDevice()).thenReturn(new Device("phoneNumber", "imei", "version"));
        when(mCredentialsRepository.getLastValidCredentials()).thenReturn(new Credentials(mCustomMockServer.getBaseEndpoint(), "test", "test"));
        when(programRepository.getUserProgram()).thenReturn(new Program("code", "uid"));
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(mCustomMockServer.getBaseEndpoint());
        ConvertToWSVisitor mConvertToWSVisitor = new ConvertToWSVisitor(deviceDataSource, mCredentialsRepository, settingsRepository, appInfoDataSource,
                new ProgramRepository(), new CountryVersionLocalDataSource());
        return new WSPushController(mConvertToWSVisitor, eReferralsAPIClient, surveyRepository);
    }

    public PushUseCase givenPushUseCase(IAppInfoRepository appInfoDataSource, IDeviceRepository deviceDataSource, ICredentialsRepository mCredentialsRepository,
                                        IProgramRepository programRepository) {
        WSPushController mWSPushController = givenWSPushController(appInfoDataSource, deviceDataSource, mCredentialsRepository, programRepository);

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

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();

                base.evaluate();

                after();
            }
        };
    }

}
