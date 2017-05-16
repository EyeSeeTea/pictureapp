package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;

public class IsLoginEnableUseCase implements UseCase {
    private IInvalidLoginAttemptsRepository mInvalidLoginAttemptsLocalDataSource;
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public IsLoginEnableUseCase(
            IInvalidLoginAttemptsRepository invalidLoginAttemptsLocalDataSource,
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor) {
        mInvalidLoginAttemptsLocalDataSource = invalidLoginAttemptsLocalDataSource;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();
        if (invalidLoginAttempts.isLoginEnabled()) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onLoginEnable();
                }
            });

        } else {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onLoginDisable();
                }
            });
        }

    }

    public interface Callback {
        void onLoginEnable();

        void onLoginDisable();
    }
}
