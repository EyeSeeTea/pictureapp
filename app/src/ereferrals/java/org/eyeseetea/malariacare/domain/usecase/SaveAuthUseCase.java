package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class SaveAuthUseCase implements UseCase {

    public interface Callback {
        void onAuthSaved(Auth auth);
    }

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAuthRepository authRepository;
    private Callback mCallback;
    private Auth mAuth;

    public SaveAuthUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAuthRepository authRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        this.authRepository = authRepository;
    }

    public void execute(Auth auth, Callback callback) {
        mAuth = auth;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                authRepository.saveAuth(mAuth);
                mCallback.onAuthSaved(mAuth);
            }
        });
    }
}
