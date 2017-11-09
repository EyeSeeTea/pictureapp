package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class GetLastInsertedCredentialsUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ICredentialsRepository mCredentialsRepository;
    private Callback mCallback;

    public GetLastInsertedCredentialsUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ICredentialsRepository credentialsRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mCredentialsRepository = credentialsRepository;
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
                mCallback.onGetUsername(mCredentialsRepository.getOrganisationCredentials());
            }
        });
    }

    public interface Callback {
        void onGetUsername(Credentials credentials);
    }
}
