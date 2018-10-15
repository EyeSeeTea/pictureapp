package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;

public class ClearCredentialasUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ICredentialsRepository mCredentialsRepository;
    private Callback mCallback;

    public interface Callback {
        void onSuccess();
    }

    public ClearCredentialasUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ICredentialsRepository credentialsRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mCredentialsRepository = credentialsRepository;
    }

    public void execute(Callback callback){
        mCallback=callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mCredentialsRepository.clearLastValidCredentials();
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess();
            }
        });
    }
}
