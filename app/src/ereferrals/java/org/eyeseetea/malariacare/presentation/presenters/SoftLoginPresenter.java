package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.boundary.executors.IDelayedMainExecutor;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.SoftLoginUseCase;

public class SoftLoginPresenter {
    private View view;
    private GetUserUserAccountUseCase getUserUserAccountUseCase;
    private SoftLoginUseCase softLoginUseCase;
    private IDelayedMainExecutor delayedMainExecutor;

    public SoftLoginPresenter(GetUserUserAccountUseCase getUserUserAccountUseCase,
            SoftLoginUseCase softLoginUseCase, IDelayedMainExecutor delayedMainExecutor) {
        this.getUserUserAccountUseCase = getUserUserAccountUseCase;
        this.softLoginUseCase = softLoginUseCase;
        this.delayedMainExecutor = delayedMainExecutor;
    }

    public void attachView(View view) {
        this.view = view;

        this.view.hideProgress();

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
                if (view != null) {
                    view.hideProgress();
                    view.loginSuccess();
                }
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

    public interface View {
        void showUsername(String username);

        void showProgress();

        void hideProgress();

        void loginSuccess();

        void showInvalidPinError();

        void showNetworkError();

        void disableLoginAction();

        void enableLoginAction();
    }
}
