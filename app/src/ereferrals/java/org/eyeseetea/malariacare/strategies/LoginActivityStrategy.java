package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.views.ViewUtils.toggleText;
import static org.eyeseetea.malariacare.views.ViewUtils.toggleVisibility;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AuthDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.exception.InvalidMetadataException;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.CheckAuthUseCase;
import org.eyeseetea.malariacare.domain.usecase.ForgotPasswordUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.IsLoginEnableUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.factories.SyncFactoryStrategy;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;

public class LoginActivityStrategy extends ALoginActivityStrategy {

    private static final java.lang.String TAG = ".LoginActivityStrategy";
    public static final java.lang.String EXIT = "exit";
    private final PullUseCase mPullUseCase;
    private IsLoginEnableUseCase mIsLoginEnableUseCase;
    private View webserviceURLContainer;
    private View programURLContainer;
    private View programEndpointContainer;
    private View webviewURLContainer;
    private EditText programURLEditText;
    private EditText programEndPointEditText;
    private EditText webviewURLEditText;
    private Spinner serverSpinner;
    private Button logoutButton;
    private Button demoButton;
    private Button advancedOptions;
    private Settings settings;
    IAsyncExecutor asyncExecutor;
    IMainExecutor mainExecutor;
    ICredentialsRepository credentialsRepository;
    IAuthRepository authRepository;
    GetSettingsUseCase getSettingsUseCase;
    SaveSettingsUseCase saveSettingsUseCase;


    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
        asyncExecutor = new AsyncExecutor();
        mainExecutor = new UIThreadExecutor();
        credentialsRepository = new CredentialsLocalDataSource();
        authRepository = new AuthDataSource(loginActivity.getApplicationContext());
        mPullUseCase = new SyncFactoryStrategy().getPullUseCase(
                loginActivity.getApplicationContext());
        ISettingsRepository settingsDataSource = new SettingsDataSource(loginActivity);
        getSettingsUseCase = new GetSettingsUseCase(new UIThreadExecutor(), new AsyncExecutor(),
                settingsDataSource);
        saveSettingsUseCase = new SaveSettingsUseCase(new UIThreadExecutor(), new AsyncExecutor(),
                settingsDataSource);
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
        runConnectVoucherFromOtherApp();
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
                //TODO: this scenario is not possible in full login, only on soft login
                Log.d(TAG, "Survey from other app with empty auth");
            }

            @Override
            public void onValidAuth() {
                //TODO: this scenario is not possible in full login, only on soft login
                Log.d(TAG, "Survey from other app with valid auth");
            }

            @Override
            public void onInValidAuth() {
                //TODO: this scenario is not possible in full login, only on soft login
                Log.d(TAG, "Survey from other app with invalid auth");
            }
        });
    }

    private void showToastAndClose(int error) {
        Toast.makeText(loginActivity, translate(error),
                Toast.LENGTH_LONG).show();
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

        initTextFields();

        initButtons();

        initSpinner();
    }

    private void initSpinner() {
        serverSpinner = (Spinner) loginActivity.findViewById(R.id.server_spinner);
        String[] serverList = loginActivity.getResources().getStringArray(R.array.server_list);
        if (serverList.length < 1) {
            return;
        }
        ArrayAdapter serversListAdapter = new ArrayAdapter<>(loginActivity.getBaseContext(),
                R.layout.item_server, serverList);
        serverSpinner.setAdapter(serversListAdapter);
        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (value.equals(
                        parent.getContext().getResources().getString(R.string.production))) {
                    setConfiguration(R.string.program_url_production,
                            R.string.program_endpoint_production, R.string.web_url_production,
                            R.string.webservice_url_production, false);
                } else if (value.equals(
                        parent.getContext().getResources().getString(R.string.training))) {
                    setConfiguration(R.string.program_url_training,
                            R.string.program_endpoint_training, R.string.web_url_training,
                            R.string.webservice_url_training, false);
                } else if (value.equals(
                        parent.getContext().getResources().getString(R.string.custom))) {
                    setConfiguration(R.string.program_url_production,
                            R.string.program_endpoint_production, R.string.web_url_production,
                            R.string.webservice_url_production, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
    }

    private void setConfiguration(int programUrlStringId, int programEndpointStringId,
            int webUrlStringId, int webserviceUrlStringId, boolean visibility) {
        showServerEditUrls(visibility);
        programURLEditText.setText(programUrlStringId);
        programEndPointEditText.setText(programEndpointStringId);
        webviewURLEditText.setText(webUrlStringId);
        loginActivity.getServerText().setText(webserviceUrlStringId);
    }

    private void showServerEditUrls(boolean value) {
        changeVisibility(webserviceURLContainer, value);
        changeVisibility(programEndpointContainer, value);
        changeVisibility(programURLContainer, value);
        changeVisibility(webviewURLContainer, value);
    }

    private void initButtons() {
        initDemoButton();

        initLogoutButton();

        initForgotPasswordButton();

        initAdvancedOptionsButton();
    }

    private void initTextFields() {
        initWebServiceURLField();
        initProgramURLField();
        initProgramEndpointField();
        initWebURLField();
        initPasswordField();
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
                        toggleSpinnerVisibility();
                        loginActivity.getUsernameEditText().setText("");
                        loginActivity.getPasswordEditText().setText("");
                        toggleVisibility(logoutButton);
                        loginActivity.getUsernameEditText().setEnabled(true);
                        Log.d(this.getClass().getSimpleName(), "onLogoutSuccess ");
                        toggleText(advancedOptions, R.string.advanced_options,
                                R.string.simple_options);
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

    private void toggleSpinnerVisibility() {
        toggleVisibility(serverSpinner);
        if (serverSpinner.getVisibility() == View.GONE) {
            showServerEditUrls(false);
        } else if (serverSpinner.getSelectedItem().equals(
                loginActivity.getString(R.string.custom))) {
            showServerEditUrls(true);
        }
    }

    private void changeVisibility(View view, boolean value) {
        if (value) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
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
                toggleVisibility(demoButton);
                toggleSpinnerVisibility();
                toggleText(advancedOptions, R.string.advanced_options, R.string.simple_options);
            }
        });
    }


    private void initWebServiceURLField() {
        webserviceURLContainer = initTextInputLayout(R.id.text_layout_webservice_server_url,
                R.string.server_url);
    }

    private void initProgramURLField() {
        programURLContainer = initTextInputLayout(R.id.text_layout_program_server_url,
                R.string.program_url);
    }

    private void initProgramEndpointField() {
        programEndpointContainer = initTextInputLayout(R.id.text_layout_program_server_endpoint,
                R.string.program_endpoint);
    }

    private void initWebURLField() {
        webviewURLContainer = initTextInputLayout(R.id.text_layout_web_server_url,
                R.string.webviews_url);
    }

    private View initTextInputLayout(int layoutId, int hintId) {
        View view = loginActivity.findViewById(layoutId);
        ((TextInputLayout) view).setHint(translate(hintId));
        return view;
    }

    @Override
    public void initProgramServer() {
        programURLEditText = loginActivity.findViewById(R.id.edittext_program_server_url);
        if (programURLEditText != null) {
            programURLEditText.setText(settings.getProgramUrl());
        }
    }

    @Override
    public void initWebviewServer() {
        webviewURLEditText = loginActivity.findViewById(R.id.edittext_web_server_url);
        if (webviewURLEditText != null) {
            webviewURLEditText.setText(settings.getWebUrl());
        }
    }

    @Override
    public void initProgramEndpoint() {
        programEndPointEditText = loginActivity.findViewById(R.id.edittext_program_server_endpoint);
        if (programEndPointEditText != null) {
            programEndPointEditText.setText(settings.getProgramEndPoint());
        }
    }

    @Override
    public void saveOtherValues(final ALoginActivityStrategy.SettingsCallback callback) {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                settings = setting;
                settings.setProgramUrl(programURLEditText.getText().toString());
                settings.setProgramEndPoint(programEndPointEditText.getText().toString());
                settings.setWebUrl(webviewURLEditText.getText().toString());
                saveSettingsUseCase.execute(new SaveSettingsUseCase.Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }
                }, settings);
            }
        });
    }

    private void initPasswordField() {
        EditText passwordEditText = loginActivity.getPasswordEditText();
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void onForgotPassword() {
        loginActivity.onStartLoading();
        ForgotPasswordUseCase forgotPasswordUseCase =
                new AuthenticationFactoryStrategy().getForgotPasswordUseCase(loginActivity);

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
                        showMessageDialog(translate(R.string.network_error),
                                translate(R.string.error_conflict_title));
                    }

                    @Override
                    public void onError(String messages) {
                        loginActivity.onFinishLoading(null);
                        showMessageDialog(messages,
                                translate(R.string.error_conflict_title));
                    }
                });


    }

    @Override
    public void onLoginSuccess(final Credentials credentials) {
        finishAndGo();
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

    public void initLoginUseCase() {
        loginActivity.mLoginUseCase = new AuthenticationFactoryStrategy()
                .getLoginUseCase(loginActivity);
    }

    @Override
    public void loadSettings(final SettingsCallback settingsCallback) {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                settings = setting;
                settingsCallback.onSuccess();
            }
        });
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
                goToDashboard();
            }

            @Override
            public void onStep(PullStep pullStep) {

            }

            @Override
            public void onError(Throwable throwable) {
                loginActivity.onFinishLoading(null);

                if (throwable instanceof InvalidMetadataException) {
                    InvalidMetadataException exception = (InvalidMetadataException) throwable;

                    switch (exception.getTypeOfFailure()) {
                        case TRANSLATIONS:
                            loginActivity.showError(
                                    R.string.error_unable_to_download_translations);
                            break;
                        case CONFIGURATION_FILES:
                            loginActivity.showError(
                                    R.string.error_unable_to_download_configuration_files);
                            break;
                        case TRANSLATIONS_AND_CONFIGURATION_FILES:
                            loginActivity.showError(
                                    R.string.error_unable_to_download_translations_and_configuration_files);
                            break;
                    }
                } else {
                    loginActivity.showError(R.string.dialog_pull_error);

                }

                executeLogout();
            }

            private void executeLogout() {
                logout(new LogoutUseCase.Callback() {
                    @Override
                    public void onLogoutSuccess() {
                        Log.e(this.getClass().getSimpleName(), "onLogoutSuccess ");
                    }

                    @Override
                    public void onLogoutError(String message) {
                        Log.e(this.getClass().getSimpleName(), "onLogoutError " + message);
                    }
                });
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
                        translate(R.string.warning_message)
                                + warning.getMessage());
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void goToDashboard() {
        loginActivity.onFinishLoading(null);
        finishAndGo(DashboardActivity.class);
    }

    private void showMessageDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(
                translate(R.string.provider_redeemEntry_msg_matchingOk),
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

                            @Override
                            public void onServerNotAvailable(String message) {
                                Log.e(this.getClass().getSimpleName(), "onServerNotAvailable error");
                            }
                        });
            }
        });
    }

    private void executePullDemo() {
        PullUseCase pullUseCase =
                new SyncFactoryStrategy().getPullUseCase(loginActivity.getApplicationContext());

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
        AlarmPushReceiver.cancelPushAlarm(loginActivity);

        LogoutUseCase logoutUseCase = new AuthenticationFactoryStrategy()
                .getLogoutUseCase(loginActivity);

        logoutUseCase.execute(callback);
    }

    private String translate(@StringRes int resourceId) {
        return loginActivity.translate(resourceId);
    }
}