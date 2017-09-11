package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.widget.EditText;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.IsLoginEnableUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;

public class LoginActivityStrategy extends ALoginActivityStrategy {

    private static final String TAG = ".LoginActivityStrategy";
    public static final String EXIT = "exit";
    private final PullUseCase mPullUseCase;
    private IsLoginEnableUseCase mIsLoginEnableUseCase;

    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
        IPullController pullController = new PullController(loginActivity);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();

        mPullUseCase = new PullUseCase(pullController, asyncExecutor, mainExecutor);
    }

    /**
     * LoginActivity does NOT admin going backwads since it is always the first activity.
     * Thus onBackPressed closes the app
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        if (loginActivity.getIntent().getBooleanExtra(EXIT, false)) {
            loginActivity.finish();
        }
    }

    public void finishAndGo(Class<? extends Activity> activityClass) {
        loginActivity.startActivity(new Intent(loginActivity, activityClass));

        loginActivity.finish();
    }

    @Override
    public void finishAndGo() {
        launchPull(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    @Override
    public void initViews() {
        EditText passwordEditText = (EditText) loginActivity.findViewById(R.id.edittext_password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        TextInputLayout passwordHint =
                (TextInputLayout) loginActivity.findViewById(R.id.password_hint);
        passwordHint.setHint(loginActivity.getResources().getText(R.string.login_pin));
    }

    @Override
    public void onLoginSuccess(final Credentials credentials) {
        loginActivity.checkAnnouncement();
    }


    @Override
    public void onBadCredentials() {
        super.onBadCredentials();
    }

    @Override
    public void disableLogin() {
        super.disableLogin();
        loginActivity.enableLogin(false);
        checkEnableLogin();
    }

    private void checkEnableLogin() {
        IInvalidLoginAttemptsRepository invalidLoginAttemptsLocalDataSource =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        mIsLoginEnableUseCase = new IsLoginEnableUseCase(
                invalidLoginAttemptsLocalDataSource, mainExecutor, asyncExecutor);
        mIsLoginEnableUseCase.execute(new IsLoginEnableUseCase.Callback() {
            @Override
            public void onLoginEnable() {
                loginActivity.enableLogin(true);
            }

            @Override
            public void onLoginDisable() {
                loginDisable();
            }
        });
    }

    private void loginDisable() {
        loginActivity.enableLogin(false);
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds
        h.postDelayed(new Runnable() {
            public void run() {
                final Runnable runnable = this;
                if (!loginActivity.isFinishing()) {
                    mIsLoginEnableUseCase.execute(new IsLoginEnableUseCase.Callback() {
                        @Override
                        public void onLoginEnable() {
                            loginActivity.enableLogin(true);
                        }

                        @Override
                        public void onLoginDisable() {
                            h.postDelayed(runnable, delay);
                        }
                    });
                }
            }
        }, delay);
    }

    @Override
    public void onStart() {
        checkEnableLogin();
    }


    public boolean canEnableLoginButtonOnTextChange() {
        IInvalidLoginAttemptsRepository invalidLoginAttemptsLocalDataSource =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        return invalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts().isLoginEnabled();
    }

    @Override
    public void onTextChange() {
        if (canEnableLoginButtonOnTextChange()) {
            super.onTextChange();
        }
    }


    public void initLoginUseCase(IAuthenticationManager authenticationManager) {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ICredentialsRepository credentialsLocalDataSoruce = new CredentialsLocalDataSource();
        IOrganisationUnitRepository organisationDataSource = new OrganisationUnitRepository();
        IInvalidLoginAttemptsRepository
                iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        loginActivity.mLoginUseCase = new LoginUseCase(authenticationManager, mainExecutor,
                asyncExecutor, organisationDataSource, credentialsLocalDataSoruce,
                iInvalidLoginAttemptsRepository);
    }

    @Override
    public void checkCredentials(Credentials credentials, final Callback callback) {
        ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
        Credentials savedCredentials = credentialsLocalDataSource.getOrganisationCredentials();
        if (savedCredentials == null || savedCredentials.isEmpty()
                || savedCredentials.getUsername().equals(
                credentials.getUsername()) && (!savedCredentials.getPassword().equals(
                credentials.getPassword()) || !savedCredentials.getServerURL().equals(
                credentials.getServerURL()))) {
            callback.onSuccessDoLogin();
        } else if (savedCredentials.getUsername().equals(
                credentials.getUsername()) && savedCredentials.getPassword().equals(
                credentials.getPassword())
                && savedCredentials.getServerURL().equals(
                credentials.getServerURL())) {
            callback.onSuccess();
        } else {
            IAuthenticationManager iAuthenticationManager = new AuthenticationManager(
                    loginActivity);
            LogoutUseCase logoutUseCase = new LogoutUseCase(iAuthenticationManager);
            AlarmPushReceiver.cancelPushAlarm(loginActivity);
            logoutUseCase.execute(new LogoutUseCase.Callback() {
                @Override
                public void onLogoutSuccess() {
                    callback.onSuccessDoLogin();
                }

                @Override
                public void onLogoutError(String message) {
                    callback.onError();
                }
            });
        }


    }


    private void launchPull(boolean isDemo) {
        PullFilters pullFilters = new PullFilters();
        pullFilters.setStartDate(PreferencesState.getInstance().getDateStarDateLimitFilter());
        pullFilters.setDownloadDataRequired(PreferencesState.getInstance().downloadDataFilter());
        pullFilters.setPullDataAfterMetadata(
                PreferencesState.getInstance().getPullDataAfterMetadata());
        pullFilters.setPullMetaData(PreferencesState.getInstance().downloadMetaData());
        if (PreferencesState.getInstance().getDataFilteredByOrgUnit()) {
            pullFilters.setDataByOrgUnit(PreferencesState.getInstance().getOrgUnit());
        }
        pullFilters.setDemo(isDemo);

        mPullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                loginActivity.onFinishLoading(null);
                finishAndGo(DashboardActivity.class);
            }

            @Override
            public void onStep(PullStep pullStep) {

            }

            @Override
            public void onError(String message) {
                loginActivity.onFinishLoading(null);
                loginActivity.showError(R.string.dialog_pull_error);
            }

            @Override
            public void onNetworkError() {
                loginActivity.onFinishLoading(null);
                loginActivity.showError(R.string.network_error);
            }

            @Override
            public void onPullConversionError() {
                loginActivity.onFinishLoading(null);
                loginActivity.showError(R.string.dialog_pull_error);
            }

            @Override
            public void onCancel() {

            }
        });

    }

}