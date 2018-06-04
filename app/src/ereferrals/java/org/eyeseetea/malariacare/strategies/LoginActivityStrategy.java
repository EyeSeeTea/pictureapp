package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.views.ViewUtils.toggleText;
import static org.eyeseetea.malariacare.views.ViewUtils.toggleVisibility;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AuthDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.LoginType;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.CheckAuthUseCase;
import org.eyeseetea.malariacare.domain.usecase.ForgotPasswordUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetLastInsertedCredentialsUseCase;
import org.eyeseetea.malariacare.domain.usecase.IsLoginEnableUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;

public class LoginActivityStrategy extends ALoginActivityStrategy {

    private static final java.lang.String TAG = ".LoginActivityStrategy";
    public static final java.lang.String EXIT = "exit";
    public static final String START_PULL = "StartPull";
    private final PullUseCase mPullUseCase;
    private IsLoginEnableUseCase mIsLoginEnableUseCase;
    private View serverURLContainer;
    private LoginType loginType;
    private Button logoutButton;
    private Button demoButton;
    private Button advancedOptions;
    IPullController pullController;
    IAsyncExecutor asyncExecutor;
    IMainExecutor mainExecutor;
    ICredentialsRepository credentialsRepository;
    IAuthRepository authRepository;

    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
        pullController = new PullController(loginActivity);
        asyncExecutor = new AsyncExecutor();
        mainExecutor = new UIThreadExecutor();
        credentialsRepository = new CredentialsLocalDataSource();
        authRepository = new AuthDataSource(loginActivity.getApplicationContext());
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
        showDashboardIfDemoUser();
        runConnectVoucherFromOtherApp();
    }

    private void showDashboardIfDemoUser() {
        GetLastInsertedCredentialsUseCase getLastInsertedCredentialsUseCase =
                new GetLastInsertedCredentialsUseCase(mainExecutor, asyncExecutor,
                        credentialsRepository);
        getLastInsertedCredentialsUseCase.execute(new GetLastInsertedCredentialsUseCase.Callback() {
            @Override
            public void onGetUsername(Credentials credentials) {
                if (credentials != null && credentials.isDemoCredentials()) {
                    finishAndGo(DashboardActivity.class);
                }
            }
        });
    }

    private void runConnectVoucherFromOtherApp() {
        CheckAuthUseCase checkAuthUseCase =
                new CheckAuthUseCase(mainExecutor, asyncExecutor,
                        authRepository, credentialsRepository);
        checkAuthUseCase.execute(new CheckAuthUseCase.Callback() {

            @Override
            public void onEmptyCredentials() {
                Log.d(TAG, "Survey from other app but empty credentials");
                showToastAndClose(R.string.no_user_error);
            }

            @Override
            public void onEmptyAuth() {
                Log.d(TAG, "Survey from other app with empty auth");
                Session.setHasSurveyToComplete(true);
            }

            @Override
            public void onValidAuth() {
                Log.d(TAG, "Survey from other app with valid auth");
                Session.setHasSurveyToComplete(true);
                finishAndGo(DashboardActivity.class);

            }

            @Override
            public void onInValidAuth() {
                Log.d(TAG, "Survey from other app with invalid auth");
                showToastAndClose(R.string.different_user_error);

            }
        });
    }

    private void showToastAndClose(int error) {
        Toast.makeText(loginActivity, error, Toast.LENGTH_LONG).show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loginActivity.onBackPressed();
            }
        };
        new Handler().postDelayed(runnable, 3500);
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
        final TextInputLayout passwordHint =
                (TextInputLayout) loginActivity.findViewById(R.id.password_hint);
        passwordHint.setHint(loginActivity.getResources().getText(R.string.login_password));

        initTextFields();

        initButtons();

        setUpSoftOrFullLoginOptions();
    }

    private void initButtons() {
        initDemoButton();

        initLogoutButton();

        initForgotPasswordButton();

        initAdvancedOptionsButton();
    }

    private void initTextFields() {
        initServerURLField();

        initPasswordField();
    }

    private void setUpSoftOrFullLoginOptions() {
        GetLastInsertedCredentialsUseCase getLastInsertedCredentialsUseCase =
                new GetLastInsertedCredentialsUseCase(mainExecutor, asyncExecutor,
                        credentialsRepository);

        getLastInsertedCredentialsUseCase.execute(
                new GetLastInsertedCredentialsUseCase.Callback() {
                    @Override
                    public void onGetUsername(Credentials credentials) {
                        //Determine if it's a Soft or full login
                        if (credentials != null) {
                            loginType = LoginType.SOFT;
                            loginActivity.getUsernameEditText().setText(credentials.getUsername());
                            loginActivity.getUsernameEditText().setEnabled(false);
                            loginActivity.getUsernameEditText().setText(credentials.getUsername());
                            CommonQuestionView.showKeyboard(loginActivity,
                                    loginActivity.getPasswordEditText());
                            loginActivity.getPasswordEditText().requestFocus();
                        } else {
                            loginType = LoginType.FULL;
                            loginActivity.getUsernameEditText().setEnabled(true);
                            loginActivity.getUsernameEditText().setText("");

                        }
                        launchUpgradeMetadataIfComeFrom209();
                    }
                });
    }


    private void launchUpgradeMetadataIfComeFrom209() {
        if (loginActivity.getIntent() != null && loginActivity.getIntent().getBooleanExtra(
                START_PULL, false)) {
            loginActivity.showProgressBar();
            launchPull(false);
            loginActivity.findViewById(R.id.progress_message).setVisibility(View.VISIBLE);
        }
    }

    private void initLogoutButton() {
        logoutButton = (Button) loginActivity.findViewById(R.id.button_log_out);

        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout(new LogoutUseCase.Callback() {
                    @Override
                    public void onLogoutSuccess() {
                        //Re-setting the state of all components
                        // for a full-login
                        toggleVisibility(serverURLContainer);
                        loginActivity.getUsernameEditText().setText("");
                        loginActivity.getPasswordEditText().setText("");
                        loginType = LoginType.FULL;
                        toggleVisibility(logoutButton);
                        loginActivity.getUsernameEditText().setEnabled(true);
                        Log.d(this.getClass().getSimpleName(), "onLogoutSuccess ");
                        toggleText(advancedOptions,R.string.advanced_options,R.string.simple_options);
                        PreferencesEReferral.setLastLoginType(loginType);
                    }

                    @Override
                    public void onLogoutError(String message) {
                        Log.e(this.getClass().getSimpleName(), "onLogoutError " + message);
                        loginActivity.showError(
                                loginActivity.getString(R.string.login_unexpected_error));
                    }
                });
            }
        });

    }

    private void initForgotPasswordButton() {
        Button forgotPassword = (Button) loginActivity.findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgotPassword();
            }
        });
    }

    private void initAdvancedOptionsButton() {
        advancedOptions = (Button) loginActivity.findViewById(R.id.advanced_options);

        advancedOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (loginType) {
                    case SOFT:
                        toggleVisibility(logoutButton);
                        break;

                    case FULL:
                        toggleVisibility(demoButton);
                        break;
                }
                toggleVisibility(serverURLContainer);
                toggleText(advancedOptions, R.string.advanced_options, R.string.simple_options);
            }
        });
    }


    private void initServerURLField() {
        serverURLContainer = loginActivity.findViewById(R.id.text_layout_server_url);
    }

    private void initPasswordField() {
        EditText passwordEditText = loginActivity.getPasswordEditText();
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        TextInputLayout passwordHint =
                (TextInputLayout) loginActivity.findViewById(R.id.password_hint);
        passwordHint.setHint(loginActivity.getResources().getText(R.string.login_password));
    }

    private void onForgotPassword() {
        loginActivity.onStartLoading();
        IAuthenticationManager authenticationManager = new AuthenticationManager(loginActivity);
        ForgotPasswordUseCase forgotPasswordUseCase = new ForgotPasswordUseCase(mainExecutor,
                asyncExecutor, authenticationManager);
        forgotPasswordUseCase.execute(loginActivity.getUsernameEditText().getText().toString(),
                new ForgotPasswordUseCase.Callback() {
                    @Override
                    public void onGetForgotPasswordSuccess(String result, String title) {
                        loginActivity.onFinishLoading(null);
                        showMessageDialog(result, title);
                    }

                    @Override
                    public void onNetworkError() {
                        loginActivity.onFinishLoading(null);
                        showMessageDialog(loginActivity.getString(R.string.network_error),
                                loginActivity.getString(R.string.error_conflict_title));
                    }

                    @Override
                    public void onError(String messages) {
                        loginActivity.onFinishLoading(null);
                        showMessageDialog(messages,
                                loginActivity.getString(R.string.error_conflict_title));
                    }
                });


    }

    @Override
    public void onLoginSuccess(final Credentials credentials) {
        loginActivity.checkAnnouncement();
        PreferencesEReferral.setLastLoginType(loginType);
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
            logout(new LogoutUseCase.Callback() {
                @Override
                public void onLogoutSuccess() {
                    callback.onSuccessDoLogin();
                }

                @Override
                public void onLogoutError(java.lang.String message) {
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
            public void onError(Throwable throwable) {
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
            public void onWarning(WarningException warning) {
                Log.w(this.getClass().getSimpleName(), "onWarning " + warning.getMessage());
                loginActivity.showError(
                        loginActivity.getString(R.string.warning_message) + warning.getMessage());
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void showMessageDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.provider_redeemEntry_msg_matchingOk,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void initDemoButton() {
        demoButton = (Button) loginActivity.findViewById(R.id.demo_login_button);

        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopulateDB.wipeDataBase();
                Credentials demoCrededentials = Credentials.createDemoCredentials();
                loginActivity.showProgressBar();
                loginActivity.mLoginUseCase.execute(demoCrededentials,
                        new ALoginUseCase.Callback() {
                            @Override
                            public void onLoginSuccess() {
                                Log.e(this.getClass().getSimpleName(), "onLoginSuccess");
                                executePullDemo();
                            }

                            @Override
                            public void onServerURLNotValid() {
                                Log.e(this.getClass().getSimpleName(),
                                        "Server url not valid");
                            }

                            @Override
                            public void onInvalidCredentials() {
                                Log.e(this.getClass().getSimpleName(),
                                        "Invalid credentials");
                            }

                            @Override
                            public void onServerPinChanged() {
                                Log.e(this.getClass().getSimpleName(), "Invalid saved Pin");
                            }

                            @Override
                            public void onNetworkError() {
                                Log.e(this.getClass().getSimpleName(), "Network Error");
                            }


                            @Override
                            public void onConfigJsonInvalid() {
                                Log.d(TAG, "onConfigJsonInvalid");
                            }

                            @Override
                            public void onUnexpectedError() {
                                Log.e(this.getClass().getSimpleName(),
                                        "Config Json file not found");
                            }

                            @Override
                            public void onMaxLoginAttemptsReachedError() {
                                Log.d(TAG, "onMaxLoginAttemptsReachedError");
                            }
                        });
            }
        });
    }

    private void executePullDemo() {
        PullController pullController = new PullController(loginActivity);
        PullUseCase pullUseCase = new PullUseCase(pullController, asyncExecutor, mainExecutor);

        PullFilters pullFilters = new PullFilters();
        pullFilters.setDemo(true);

        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                loginActivity.hideProgressBar();
                finishAndGo(DashboardActivity.class);
            }

            @Override
            public void onStep(PullStep step) {
                Log.d(this.getClass().getSimpleName(), step.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), throwable.getMessage());
            }

            @Override
            public void onPullConversionError() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Pull conversion error");
            }

            @Override
            public void onWarning(WarningException warning) {
                Log.w(this.getClass().getSimpleName(), "onWarning " + warning.getMessage());
            }

            @Override
            public void onCancel() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Pull cancel");
            }

            @Override
            public void onNetworkError() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Network Error");
            }
        });
    }

    private void logout(final LogoutUseCase.Callback callback) {
        IAuthenticationManager iAuthenticationManager = new AuthenticationManager(
                loginActivity);

        AlarmPushReceiver.cancelPushAlarm(loginActivity);

        LogoutUseCase logoutUseCase = new LogoutUseCase(iAuthenticationManager);

        logoutUseCase.execute(callback);
    }
}