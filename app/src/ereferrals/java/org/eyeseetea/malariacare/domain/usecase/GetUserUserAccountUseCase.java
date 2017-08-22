package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class GetUserUserAccountUseCase implements UseCase {
    private static final String TAG = "GetUserUserAccountUC";
    private Callback mCallback;
    private IUserRepository mUserAccountLocalDataSource;

    public GetUserUserAccountUseCase(
            IUserRepository userAccountLocalDataSource) {
        mUserAccountLocalDataSource = userAccountLocalDataSource;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        run();
    }

    @Override
    public void run() {
        mCallback.onGetUserAccount(mUserAccountLocalDataSource.getLoggedUser());
    }

    public interface Callback {
        void onGetUserAccount(UserAccount userAccount);
    }
}
