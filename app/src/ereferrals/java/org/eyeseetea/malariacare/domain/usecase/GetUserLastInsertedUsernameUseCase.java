package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;

public class GetUserLastInsertedUsernameUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ICredentialsRepository mCredentialsRepository;
    private Callback mCallback;

    public GetUserLastInsertedUsernameUseCase(
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
        final String username = mCredentialsRepository.getOrganisationCredentials().getUsername();
        if (!username.isEmpty()) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onGetUsername(username);
                }
            });
        }
    }

    public interface Callback {
        void onGetUsername(String username);
    }
}
