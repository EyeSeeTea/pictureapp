package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IRepositoryCallback;
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
        mUserAccountRepository.logout(
                new IRepositoryCallback<Void>() {
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
