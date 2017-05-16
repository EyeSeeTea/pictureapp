package org.eyeseetea.malariacare.services.strategies;

import android.content.Intent;
import android.util.Log;

import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
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
}
