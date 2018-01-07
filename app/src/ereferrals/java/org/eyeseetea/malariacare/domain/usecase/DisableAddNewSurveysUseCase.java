package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class DisableAddNewSurveysUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IUserRepository mUserDataSource;
    private Callback mCallback;

    public DisableAddNewSurveysUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IUserRepository userDataSource) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mUserDataSource = userDataSource;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        UserAccount userAccount = mUserDataSource.getLoggedUser();
        userAccount.setCanAddSurveys(false);
        mUserDataSource.saveLoggedUser(userAccount);
        notifyOnSuccess();
    }

    private void notifyOnSuccess() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess();
            }
        });
    }


    public interface Callback {
        void onSuccess();

        void onError();
    }
}
