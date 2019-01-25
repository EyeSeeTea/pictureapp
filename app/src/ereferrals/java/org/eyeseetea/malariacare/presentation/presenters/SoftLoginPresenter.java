package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.boundary.executors.IDelayedMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
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

    boolean showingAdvancedOptions = false;

    public SoftLoginPresenter(GetUserUserAccountUseCase getUserUserAccountUseCase,
            SoftLoginUseCase softLoginUseCase,
            LogoutUseCase logoutUseCase,
            IDelayedMainExecutor delayedMainExecutor,
            GetSettingsUseCase getSettingsUseCase, SaveSettingsUseCase saveSettingsUseCase) {
        this.getUserUserAccountUseCase = getUserUserAccountUseCase;
        this.softLoginUseCase = softLoginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.delayedMainExecutor = delayedMainExecutor;
        this.getSettingsUseCase = getSettingsUseCase;
        this.saveSettingsUseCase = saveSettingsUseCase;
    }

    public void attachView(View view) {
        this.view = view;

        this.view.hideProgress();
        this.view.hideAdvancedOptions();

        loadCurrentUser();
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
                saveSettings(settings);

                if (view != null) {
                    view.hideProgress();
                    view.loginSuccess();
                }
            }
        });
    }

    private void saveSettings(Settings settings) {
        settings.changeSoftLoginRequired(false);
        saveSettingsUseCase.execute(new SaveSettingsUseCase.Callback() {
            @Override
            public void onSuccess() {
                System.out.println("Saved - Soft Login is not required");
            }
        }, settings);
    }

    private void disableLoginActionUntilEnableLoginTime(final long enableLoginTime) {
        if (view != null) {
            view.hideProgress();
            view.disableLoginAction();
        }

        verifyEnableLogin(enableLoginTime);
    }

    private void verifyEnableLogin(final long enableLoginTime) {
        delayedMainExecutor.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= enableLoginTime) {
                    if (view != null) {
                        view.enableLoginAction();
                    }
                } else {
                    verifyEnableLogin(enableLoginTime);
                }
            }
        }, 1000);
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
                    changeSoftLoginToNotRequired();
                    view.navigateToLogin();
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

    private void changeSoftLoginToNotRequired() {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings settings) {
                settings.changeSoftLoginRequired(false);
                saveSettings(settings);
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

        void loginSuccess();

        void showInvalidPinError();

        void showNetworkError();

        void disableLoginAction();

        void enableLoginAction();

        void showAdvancedOptions();

        void hideAdvancedOptions();

        void navigateToLogin();

        void showLogoutError();
    }
}
