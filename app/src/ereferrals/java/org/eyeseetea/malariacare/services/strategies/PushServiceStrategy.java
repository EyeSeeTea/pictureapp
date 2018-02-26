package org.eyeseetea.malariacare.services.strategies;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.utils.Permissions;

public class PushServiceStrategy extends APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";
    public static final String SERVICE_METHOD = "serviceMethod";
    public static final String PUSH_MESSAGE = "PushStart";
    public static final String PUSH_IS_START = "PushIsStart";
    public static final String SHOW_LOGIN = "ShowLogin";

    private IPushController mPushController;
    private PushUseCase mPushUseCase;

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {

        if (Permissions.isPhonePermissionGranted(PreferencesState.getInstance().getContext())) {
            Log.w(getClass().getSimpleName(), "Push cancelled because does not exist phone permissions");
            return;
        }

        ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();

        Credentials credentials = credentialsLocalDataSource.getCredentials();

        if (credentials != null) {
            if (credentials.isDemoCredentials()) {
                PushServiceStrategy.this.onCorrectCredentials();
            } else {
                IAuthenticationManager authenticationManager = new AuthenticationManager(
                        PreferencesState.getInstance().getContext());
                IMainExecutor mainExecutor = new UIThreadExecutor();
                IAsyncExecutor asyncExecutor = new AsyncExecutor();

                IOrganisationUnitRepository organisationDataSource =
                        new OrganisationUnitRepository();
                IInvalidLoginAttemptsRepository
                        iInvalidLoginAttemptsRepository =
                        new InvalidLoginAttemptsRepositoryLocalDataSource();
                LoginUseCase loginUseCase = new LoginUseCase(authenticationManager, mainExecutor,
                        asyncExecutor, organisationDataSource, credentialsLocalDataSource,
                        iInvalidLoginAttemptsRepository);
                final Credentials oldCredentials =
                        credentialsLocalDataSource.getOrganisationCredentials();
                loginUseCase.execute(oldCredentials, new ALoginUseCase.Callback() {
                    @Override
                    public void onLoginSuccess() {
                        Log.e(TAG, "onLoginSuccess");
                        PushServiceStrategy.this.onCorrectCredentials();
                    }

                    @Override
                    public void onServerURLNotValid() {
                        Log.e(TAG, "Error getting user credentials: URL not valid ");
                    }

                    @Override
                    public void onServerPinChanged() {
                        Log.e(TAG, "Error onServerPinChanged");
                        AlarmPushReceiver.cancelPushAlarm(mPushService);
                        moveToLoginActivity();
                    }

                    @Override
                    public void onInvalidCredentials() {
                        Log.e(TAG, "Error credentials not valid.");
                        AlarmPushReceiver.cancelPushAlarm(mPushService);
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
                        Log.e(TAG, "onMaxLoginAttemptsReachedError");
                    }
                });
            }
        } else {
            Log.w(TAG, "Push cancelled because does not exist user credentials, possible logout");
        }

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
        if (PreferencesState.getCredentialsFromPreferences().isDemoCredentials()) {
            executeMockedPush();
        } else {
            executePush();
        }
    }


    public void logout() {
        IAuthenticationManager authenticationManager;
        LogoutUseCase logoutUseCase;
        authenticationManager = new AuthenticationManager(mPushService);
        logoutUseCase = new LogoutUseCase(authenticationManager);
        logoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                moveToLoginActivity();
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
    }

    private void moveToLoginActivity() {
        if (!EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            Intent loginIntent = new Intent(mPushService, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mPushService.startActivity(loginIntent);
        }
    }

    public void executePush(PushUseCase pushUseCase) {
        mPushUseCase = pushUseCase;
        executePush();
    }

    protected void executePush() {
        IPushController pushController = null;
        if (mPushUseCase == null) {
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
        }
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);

        PushUseCase pushUseCase = mPushUseCase;
        if (pushUseCase == null) {
            pushUseCase = new PushUseCase(pushController, asyncExecutor, mainExecutor,
                    surveysThresholds, surveyRepository, orgUnitRepository);
        }


        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {
                Log.d(TAG, "OnPushStart");
                sendIntentStartEndPush(true);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "PUSHUSECASE WITHOUT ERROR push complete");
                mPushService.onPushFinished();
                sendIntentStartEndPush(false);
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
                onError("PUSHUSECASE ERROR Pending surveys not found");
            }

            @Override
            public void onConversionError() {
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title),
                        PreferencesState.getInstance().getContext().getString(
                                R.string.ws_conversion_error));
            }

            @Override
            public void onNetworkError() {
                onError("PUSHUSECASE ERROR Network not available");
            }

            @Override
            public void onInformativeError(String message) {
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title), "PUSHUSECASE ERROR " + message
                        +" "+ PreferencesState.getInstance().isPushInProgress());
            }

            @Override
            public void onInformativeMessage(String message) {
                showInDialog("", message);
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
                onError("PUSHUSECASE ERROR " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onClosedUser() {
                onError("PUSHUSECASE ERROR on closedUser "
                        + PreferencesState.getInstance().isPushInProgress());
                closeUserLogout();
            }
        });
    }

    @Override
    public void showInDialog(String title, String message) {
        super.showInDialog(title, message);
        sendIntentStartEndPush(false);
    }

    @Override
    public void onError(String error) {
        super.onError(error);
        sendIntentStartEndPush(false);
    }

    private void sendIntentStartEndPush(boolean start) {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(SERVICE_METHOD, PUSH_MESSAGE);
        surveysIntent.putExtra(PUSH_IS_START, start);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }

    private void sendIntentShowLogin() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(SERVICE_METHOD, PUSH_MESSAGE);
        surveysIntent.putExtra(SHOW_LOGIN, true);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }
}
