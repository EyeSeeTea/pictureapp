package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;

public class LogoutUseCase {
    public interface Callback {
        void onLogoutSuccess();

        void onLogoutError(String message);
    }

    private IAuthenticationManager mIAuthenticationManager;

    public LogoutUseCase(IAuthenticationManager authenticationManager) {
        mIAuthenticationManager = authenticationManager;
    }

    public void execute(final Callback callback) {
        mIAuthenticationManager.logout(
                new IAuthenticationManager.Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        callback.onLogoutSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onLogoutError(throwable.getMessage());
                    }
                });
    }
}
