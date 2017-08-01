package org.eyeseetea.malariacare.services.strategies;

import android.content.Intent;
import android.util.Log;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.network.SurveyChecker;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {

        IAuthenticationManager authenticationManager = new AuthenticationManager(
                PreferencesState.getInstance().getContext());
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ICredentialsRepository credentialsLocalDataSoruce = new CredentialsLocalDataSource();
        IOrganisationUnitRepository organisationDataSource = new OrganisationUnitRepository();
        IInvalidLoginAttemptsRepository
                iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        LoginUseCase loginUseCase = new LoginUseCase(authenticationManager, mainExecutor,
                asyncExecutor, organisationDataSource, credentialsLocalDataSoruce,
                iInvalidLoginAttemptsRepository);
        final Credentials oldCredentials = credentialsLocalDataSoruce.getOrganisationCredentials();
        loginUseCase.execute(oldCredentials, new ALoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                PushServiceStrategy.this.onCorrectCredentials();
            }

            @Override
            public void onServerURLNotValid() {
                Log.e(TAG, "Error getting user credentials: URL not valid ");
            }

            @Override
            public void onInvalidCredentials() {
                logout();
            }

            @Override
            public void onNetworkError() {
                Log.e(TAG, "Error getting user credentials: NetworkError");
            }

            @Override
            public void onConfigJsonInvalid() {
                Log.e(TAG, "Error getting user credentials: JsonInvalid");
            }

            @Override
            public void onUnexpectedError() {
                Log.e(TAG, "Error getting user credentials: unexpectedError ");
            }

            @Override
            public void onMaxLoginAttemptsReachedError() {

            }
        });
    }

    protected void executeMockedPush() {
        IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
        MockedPushSurveysUseCase mockedPushSurveysUseCase = new MockedPushSurveysUseCase(
                programLocalDataSource);

        mockedPushSurveysUseCase.execute(new MockedPushSurveysUseCase.Callback() {
            @Override
            public void onPushFinished() {
                Log.d(TAG, "onPushMockFinished");
                mPushService.onPushFinished();
            }
        });
    }

    private void onCorrectCredentials() {
        executePush();
    }


    public void logout() {
        IAuthenticationManager authenticationManager;
        LogoutUseCase logoutUseCase;
        authenticationManager = new AuthenticationManager(mPushService);
        logoutUseCase = new LogoutUseCase(authenticationManager);
        AlarmPushReceiver.cancelPushAlarm(mPushService);
        logoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                if (!EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
                    Intent loginIntent = new Intent(mPushService, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mPushService.startActivity(loginIntent);
                }
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
    }

    protected void executePush() {
        IPushController pushController;
        try {
            pushController = new WSPushController();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            showInDialog(PreferencesState.getInstance().getContext().getString(
                    R.string.webservice_url_error_title),
                    String.format(PreferencesState.getInstance().getContext().getString(
                            R.string.webservice_url_error), e.getMessage()));
            return;
        }
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);

        PushUseCase pushUseCase =
                new PushUseCase(pushController, asyncExecutor, mainExecutor,
                        surveysThresholds, surveyRepository, orgUnitRepository);

        SurveyChecker.launchQuarantineChecker();

        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "PUSHUSECASE WITHOUT ERROR push complete");
                mPushService.onPushFinished();
            }

            @Override
            public void onPushInProgressError() {
                Log.d(TAG, "PUSHUSECASE ERROR Push stopped, There is already a push in progress");
            }

            @Override
            public void onPushError() {
                onError("PUSHUSECASE ERROR Unexpected error has occurred in push process");
            }

            @Override
            public void onSurveysNotFoundError() {
                onError("PUSHUSECASE ERROR Pending surveys not found");}

            @Override
            public void onConversionError() {
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title),
                        PreferencesState.getInstance().getContext().getString(
                                R.string.ws_conversion_error));
            }

            @Override
            public void onNetworkError() {
                onError("PUSHUSECASE ERROR Network not available");}

            @Override
            public void onInformativeError(String message) {
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title), "PUSHUSECASE ERROR "+message + PreferencesState.getInstance().isPushInProgress());
            }

            @Override
            public void onBannedOrgUnit() {
                showInDialog("", PreferencesState.getInstance().getContext().getString(
                        R.string.exception_org_unit_banned));
            }

            @Override
            public void onReOpenOrgUnit() {
                showInDialog("",
                        String.format(PreferencesState.getInstance().getContext().getString(
                                R.string.dialog_reopen_org_unit),
                                PreferencesState.getInstance().getOrgUnit()));
            }

            @Override
            public void onApiCallError() {
                onError("API call error");
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                onError("PUSHUSECASE ERROR "+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onClosedUser() {
                onError("PUSHUSECASE ERROR on closedUser "+PreferencesState.getInstance().isPushInProgress());
                closeUserLogout();
            }
        });
    }
}
