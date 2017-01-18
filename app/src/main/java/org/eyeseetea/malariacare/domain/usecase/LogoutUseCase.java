package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IUserAccountRepository;

public class LogoutUseCase {
    public interface Callback {
        void onLogoutSuccess();

        void onLogoutError(String message);
    }

    private IUserAccountRepository mUserAccountRepository;

    public LogoutUseCase(IUserAccountRepository userAccountRepository) {
        mUserAccountRepository = userAccountRepository;
    }

    public void execute(final Callback callback) {
        mUserAccountRepository.removeCurrentUserAccount(
                new IUserAccountRepository.RemoveCurrentUserAccountCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onLogoutSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onLogoutError(throwable.getMessage());
                    }
                });
    }
}
