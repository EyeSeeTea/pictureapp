package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class GetAuthUseCase implements UseCase {

    public interface Callback {
        void onGetAuth(Auth auth);
    }

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAuthRepository authRepository;
    private Callback mCallback;

    public GetAuthUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAuthRepository authRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        this.authRepository = authRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetAuth(authRepository.getAuth());
            }
        });
    }
}
