package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IDelayedMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;
import org.eyeseetea.malariacare.domain.usecase.CheckAuthUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveAuthUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.SoftLoginUseCase;

public class SoftLoginPresenter {
    private View view;
    private final GetUserUserAccountUseCase getUserUserAccountUseCase;
    private final SoftLoginUseCase softLoginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final IDelayedMainExecutor delayedMainExecutor;
    private final GetSettingsUseCase getSettingsUseCase;
    private final SaveSettingsUseCase saveSettingsUseCase;
    private final CheckAuthUseCase checkExternalAuthUseCase;
    private final SaveAuthUseCase saveAuthUseCase;

    private boolean showingAdvancedOptions = false;

    private boolean isLoginDisabledByMaxInvalidLoginAttempts = false;
    private boolean isLoginDisabledByEmptyPin = false;


    public SoftLoginPresenter(GetUserUserAccountUseCase getUserUserAccountUseCase,
            SoftLoginUseCase softLoginUseCase,
            LogoutUseCase logoutUseCase,
            IDelayedMainExecutor delayedMainExecutor,
            GetSettingsUseCase getSettingsUseCase,
            SaveSettingsUseCase saveSettingsUseCase,
            CheckAuthUseCase checkExternalAuthUseCase,
            SaveAuthUseCase saveAuthUseCase) {
        this.getUserUserAccountUseCase = getUserUserAccountUseCase;
        this.softLoginUseCase = softLoginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.delayedMainExecutor = delayedMainExecutor;
        this.getSettingsUseCase = getSettingsUseCase;
        this.saveSettingsUseCase = saveSettingsUseCase;
        this.checkExternalAuthUseCase = checkExternalAuthUseCase;
        this.saveAuthUseCase = saveAuthUseCase;
    }

    public void attachView(View view) {
        this.view = view;

        this.view.hideProgress();
        this.view.hideAdvancedOptions();

        disableLoginAction();
        loadCurrentUser();
        verifySoftLoginFromOtherApp();
    }

    private void verifySoftLoginFromOtherApp() {
        checkExternalAuthUseCase.execute(new CheckAuthUseCase.Callback() {

            @Override
            public void onEmptyCredentials() {
                //TODO: this scenario is not possible in soft login, only on full login
                System.out.println(
                        "verifyIfSoftLoginFromOtherApp: Survey from other app but empty "
                                + "credentials");
            }

            @Override
            public void onEmptyAuth() {
                System.out.println(
                        "verifyIfSoftLoginFromOtherApp: Survey from other app with empty auth");
                //TODO: We should not use session from here, on the future we should save it on
                // settings
                Session.setHasSurveyToComplete(true);
            }

            @Override
            public void onValidAuth() {
                System.out.println(
                        "verifyIfSoftLoginFromOtherApp: Survey from other app with valid auth");

                //TODO: We should not use session from here, on the future we should save it on
                // settings
                Session.setHasSurveyToComplete(true);

                resetAfterValidExternalAuthAndNotifySuccess();
            }

            @Override
            public void onInValidAuth() {
                System.out.println(
                        "verifyIfSoftLoginFromOtherApp: Survey from other app with invalid auth");

                if (view != null) {
                    view.showInvalidAuthFromExternalApp();
                }
            }
        });
    }

    private void resetAfterValidExternalAuthAndNotifySuccess() {
        saveAuthUseCase.execute(null, new SaveAuthUseCase.Callback() {
            @Override
            public void onAuthSaved(Auth auth) {
                Runnable actionOnSaveSettingsSuccess = new Runnable() {
                    @Override
                    public void run() {
                        if (view != null) {
                            view.softLoginSuccess();
                        }
                    }
                };

                changeSoftLoginToNotRequired(actionOnSaveSettingsSuccess);
            }
        });
    }

    public void detachView() {
        view = null;
    }

    public void login(String pin) {
        if (view != null) {
            view.showProgress();
        }

        softLoginUseCase.execute(pin, new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                verifyIfLaunchPull();
            }

            @Override
            public void onInvalidPin() {
                if (view != null) {
                    view.hideProgress();
                    view.showInvalidPinError();
                }
            }

            @Override
            public void onNetworkError() {
                if (view != null) {
                    view.hideProgress();
                    view.showNetworkError();
                }
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                disableLoginActionUntilEnableLoginTime(enableLoginTime);

            }

            @Override
            public void onServerNotAvailable(String message) {
                if (view != null) {
                    view.hideProgress();
                    view.showServerNotAvailable(message);
                }
            }
        });
    }

    private void verifyIfLaunchPull() {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings settings) {
                settings.changeSoftLoginRequired(false);
                if (settings.isMetadataUpdateActive()) {
                    settings.changePullRequired(true);
                }

                Runnable actionOnSaveSettingsSuccess = new Runnable() {
                    @Override
                    public void run() {
                        if (view != null) {
                            view.hideProgress();
                            view.softLoginSuccess();
                        }
                    }
                };

                saveSettings(settings, actionOnSaveSettingsSuccess);


            }
        });
    }

    private void saveSettings(Settings settings, final Runnable actionOnSaveSettingsSuccess) {
        saveSettingsUseCase.execute(new SaveSettingsUseCase.Callback() {
            @Override
            public void onSuccess() {
                System.out.println("Saved - Soft Login is not required");
                if (actionOnSaveSettingsSuccess != null) {
                    actionOnSaveSettingsSuccess.run();
                }
            }
        }, settings);
    }

    public void onPinChanged(String pin) {
        if (pin == null || pin.isEmpty()) {
            isLoginDisabledByEmptyPin = true;
            disableLoginAction();
        } else {
            isLoginDisabledByEmptyPin = false;

            if (isLoginDisabledByMaxInvalidLoginAttempts == false) {
                enableLoginAction();
            }
        }
    }

    private void disableLoginActionUntilEnableLoginTime(final long enableLoginTime) {
        disableLoginAction();
        isLoginDisabledByMaxInvalidLoginAttempts = true;

        verifyEnableLogin(enableLoginTime);
    }

    private void disableLoginAction() {
        if (view != null) {
            view.hideProgress();
            view.disableLoginAction();
        }
    }

    private void verifyEnableLogin(final long enableLoginTime) {
        delayedMainExecutor.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= enableLoginTime) {
                    isLoginDisabledByMaxInvalidLoginAttempts = false;

                    enableLoginAction();
                } else {
                    verifyEnableLogin(enableLoginTime);
                }
            }
        }, 1000);
    }

    private void enableLoginAction() {
        if (view != null && !isLoginDisabledByEmptyPin &&
                !isLoginDisabledByMaxInvalidLoginAttempts) {
            view.enableLoginAction();
        }
    }

    private void loadCurrentUser() {
        getUserUserAccountUseCase.execute(new GetUserUserAccountUseCase.Callback() {
            @Override
            public void onGetUserAccount(UserAccount userAccount) {
                if (view != null) {
                    view.showUsername(userAccount.getUserName());
                }
            }
        });
    }

    public void logout() {
        logoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                if (view != null) {
                    Runnable actionOnSaveSettingsSuccess = new Runnable() {
                        @Override
                        public void run() {
                            if (view != null) {
                                view.navigateToLogin();
                            }
                        }
                    };

                    changeSoftLoginToNotRequired(actionOnSaveSettingsSuccess);

                }
            }

            @Override
            public void onLogoutError(String message) {
                if (view != null) {
                    view.showLogoutError();
                }
            }
        });
    }

    private void changeSoftLoginToNotRequired(final Runnable actionOnSaveSettingsSuccess) {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings settings) {
                settings.changeSoftLoginRequired(false);
                saveSettings(settings, actionOnSaveSettingsSuccess);
            }
        });
    }

    public void advancedOptions() {
        if (view != null) {
            if (showingAdvancedOptions) {
                view.hideAdvancedOptions();
                showingAdvancedOptions = false;
            } else {
                view.showAdvancedOptions();
                showingAdvancedOptions = true;
            }
        }
    }

    public interface View {
        void showUsername(String username);

        void showProgress();

        void hideProgress();

        void softLoginSuccess();

        void showInvalidPinError();

        void showNetworkError();

        void disableLoginAction();

        void enableLoginAction();

        void showAdvancedOptions();

        void hideAdvancedOptions();

        void navigateToLogin();

        void showLogoutError();

        void showInvalidAuthFromExternalApp();

        void showServerNotAvailable(String message);
    }
}
