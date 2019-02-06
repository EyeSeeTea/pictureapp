package org.eyeseetea.malariacare.services.strategies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.domain.usecase.GetAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.factories.AppInfoFactory;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.factories.SyncFactoryStrategy;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.utils.Permissions;

import java.util.Date;

public class PushServiceStrategy extends APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";
    public static final String SERVICE_METHOD = "serviceMethod";
    public static final String PUSH_MESSAGE = "PushStart";
    public static final String PUSH_IS_START = "PushIsStart";
    public static final String PULL_REQUIRED = "PullRequired";
    public static final String INVALID_CREDENTIALS_ON_PUSH = "InvalidCredentialsOnPush";
    public static final String PUSH_NETWORK_ERROR = "PushNetworkError";

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

        ICredentialsRepository credentialsRepository =
                new AuthenticationFactoryStrategy().getCredentialsRepository();

        Credentials credentials = credentialsRepository.getCredentials();

        if (credentials != null) {
            if (PreferencesState.getCredentialsFromPreferences().isDemoCredentials()) {
                executeMockedPush();
            } else {
                executePush();
            }
            updateAppInfo(mPushService);
        } else {
            Log.w(TAG, "Push cancelled because does not exist user credentials, possible logout");
        }

    }

    protected void executeMockedPush() {
        IProgramRepository programRepository = new ProgramRepository();
        MockedPushSurveysUseCase mockedPushSurveysUseCase = new MockedPushSurveysUseCase(
                programRepository);
        sendIntentStartEndPush(true);
        mockedPushSurveysUseCase.execute(new MockedPushSurveysUseCase.Callback() {
            @Override
            public void onPushFinished() {
                Log.d(TAG, "onPushMockFinished");
                sendIntentStartEndPush(false);
                mPushService.onPushFinished();
            }
        });
    }

    private void updateAppInfo(final Context context) {
        final AppInfoFactory appInfoFactory = new AppInfoFactory();
        appInfoFactory.getGetAppInfoUseCase(context).execute(new GetAppInfoUseCase.Callback() {
            @Override
            public void onAppInfoLoaded(AppInfo appInfo) {
                SaveAppInfoUseCase saveAppInfoUseCase = appInfoFactory.getSaveAppInfoUseCase(
                        context);
                saveAppInfoUseCase.excute(new SaveAppInfoUseCase.Callback() {
                    @Override
                    public void onAppInfoSaved() {
                    }
                }, new AppInfo(appInfo.getMetadataVersion(), appInfo.getConfigFileVersion(),
                        appInfo.getAppVersion(), appInfo.getUpdateMetadataDate(), new Date()));
            }
        });
    }


    public void logout() {
        LogoutUseCase logoutUseCase = new AuthenticationFactoryStrategy()
                .getLogoutUseCase(mPushService);

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
        if (!EyeSeeTeaApplication.getInstance().isAppInBackground()) {
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
        PushUseCase pushUseCase = mPushUseCase;
        if (pushUseCase == null) {
            try {
                pushUseCase = new SyncFactoryStrategy()
                        .getPushUseCase(mPushService.getApplicationContext());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.webservice_url_error_title),
                        String.format(PreferencesState.getInstance().getContext().getString(
                                R.string.webservice_url_error), e.getMessage()));
                return;
            }
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
                sendIntentNetwokError();
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
                handleAPIException(e);
                onError("PUSHUSECASE ERROR " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onInvalidCredentials() {
                sendInvalidcredentialsOnPush();
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

    private void sendIntentNetwokError() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(SERVICE_METHOD, PUSH_MESSAGE);
        surveysIntent.putExtra(PUSH_NETWORK_ERROR, true);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }

    private void handleAPIException(Exception e) {
        if (e instanceof ConfigFileObsoleteException) {
            sendPullRequired();
        }
    }

    private void sendPullRequired() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(SERVICE_METHOD, PUSH_MESSAGE);
        surveysIntent.putExtra(PULL_REQUIRED, true);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }

    private void sendInvalidcredentialsOnPush() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(SERVICE_METHOD, PUSH_MESSAGE);
        surveysIntent.putExtra(INVALID_CREDENTIALS_ON_PUSH, true);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }
}
