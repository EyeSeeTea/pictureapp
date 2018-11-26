package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class CheckAuthUseCase implements UseCase {

    public interface Callback {
        void onEmptyCredentials();
        void onEmptyAuth();
        void onValidAuth();
        void onInValidAuth();

    }

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAuthRepository mAuthRepository;
    private ICredentialsRepository mCredentialsRepository;
    private Callback mCallback;

    public CheckAuthUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAuthRepository authRepository,
            ICredentialsRepository credentialsRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAuthRepository = authRepository;
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
                Auth auth = mAuthRepository.getAuth();

                if(auth==null){
                    //auth voucher from other app doesn't exist.
                    return;
                }

                Credentials credentials = mCredentialsRepository.getOrganisationCredentials();
                if(!hasCredentials(credentials)){
                    mCallback.onEmptyCredentials();
                    return;
                }

                if(auth.hasAuth()){
                    if(isValidUserAndPassword(credentials, auth)){
                        mCallback.onValidAuth();
                        return;
                    }else {
                        mCallback.onInValidAuth();
                        return;
                    }
                }
                mCallback.onEmptyAuth();
            }
        });
    }

    private boolean hasCredentials(Credentials credentials) {
        return credentials != null && !credentials.isEmpty();
    }

    private boolean isValidUserAndPassword(Credentials credentials, Auth auth) {
        return auth.getUserName().equals(credentials.getUsername())
                && auth.getPassword().equals(credentials.getPassword());
    }
}
