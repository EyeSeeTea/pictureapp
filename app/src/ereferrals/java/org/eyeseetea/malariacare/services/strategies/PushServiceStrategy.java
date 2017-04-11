package org.eyeseetea.malariacare.services.strategies;

import android.content.Intent;
import android.util.Log;

import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.importer.PullOrganisationCredentialsController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.CheckCredentialsWithOrgUnitUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.PullOrganisationCredentialsUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {
        final Credentials oldCredentials = PreferencesEReferral.getUserCredentialsFromPreferences();
        PullOrganisationCredentialsController pullOrganisationCredentialsController =
                new PullOrganisationCredentialsController(oldCredentials, mPushService);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        PullOrganisationCredentialsUseCase pullOrganisationCredentialsUseCase =
                new PullOrganisationCredentialsUseCase(asyncExecutor, mainExecutor,
                        pullOrganisationCredentialsController);
        pullOrganisationCredentialsUseCase.execute(
                new PullOrganisationCredentialsUseCase.Callback() {
                    @Override
                    public void onComplete() {
                        CheckCredentialsWithOrgUnitUseCase checkCredentialsWithOrgUnitUseCase =
                                new CheckCredentialsWithOrgUnitUseCase();
                        checkCredentialsWithOrgUnitUseCase.execute(oldCredentials,
                                new CheckCredentialsWithOrgUnitUseCase.Callback() {
                                    @Override
                                    public void onCorrectCredentials() {
                                        PushServiceStrategy.this.onCorrectCredentials();
                                    }

                                    @Override
                                    public void onBadCredentials(boolean b) {
                                        logout();
                                    }
                                });
                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, "Error getting user credentials: " + message);
                    }

                    @Override
                    public void onNetworkError() {
                        Log.e(TAG, "Error getting user credentials: NetworkError");
                    }

                    @Override
                    public void onPullConversionError() {
                        Log.e(TAG, "Error getting user credentials: PullConversionError");
                    }

                    @Override
                    public void onInvalidCredentials() {
                        Log.e(TAG,
                                "Error getting user credentials: Invalid credentials wrong username");
                    }

                });

    }

    protected void executeMockedPush() {
        MockedPushSurveysUseCase mockedPushSurveysUseCase = new MockedPushSurveysUseCase();

        mockedPushSurveysUseCase.execute(new MockedPushSurveysUseCase.Callback() {
            @Override
            public void onPushFinished() {
                Log.d(TAG, "onPushMockFinished");
                mPushService.onPushFinished();
            }
        });
    }

    private void onCorrectCredentials() {
        if (Session.getCredentials().isDemoCredentials()) {
            Log.d(TAG, "execute push");
            executeMockedPush();
        } else {
            Log.d(TAG, "execute push fails, not logged");
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
}
