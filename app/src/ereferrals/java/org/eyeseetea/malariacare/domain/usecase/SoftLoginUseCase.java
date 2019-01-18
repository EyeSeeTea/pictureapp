package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ActionNotAllowed;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;

public class SoftLoginUseCase implements UseCase {
    private final IConnectivityManager connectivityManager;
    private final IAuthenticationManager authenticationManager;
    private final IMainExecutor mainExecutor;
    private final IAsyncExecutor asyncExecutor;
    private final ICredentialsRepository credentialsLocalDataSource;
    private final IInvalidLoginAttemptsRepository invalidLoginAttemptsLocalDataSource;

    private String softLoginPin;
    private Credentials lastValidCredentials = null;

    private Callback mCallback;

    public SoftLoginUseCase(IConnectivityManager connectivityManager,
            IAuthenticationManager authenticationManager, IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor, ICredentialsRepository credentialsLocalDataSource,
            IInvalidLoginAttemptsRepository invalidLoginAttemptsRepository) {
        this.connectivityManager = connectivityManager;
        this.authenticationManager = authenticationManager;
        this.mainExecutor = mainExecutor;
        this.asyncExecutor = asyncExecutor;
        this.credentialsLocalDataSource = credentialsLocalDataSource;
        this.invalidLoginAttemptsLocalDataSource = invalidLoginAttemptsRepository;
    }

    public void execute(String pin, final Callback callback) {
        mCallback = callback;
        this.softLoginPin = pin;
        asyncExecutor.run(this);
    }

    @Override
    public void run() {

        if (isLoginEnable()) {
            lastValidCredentials = credentialsLocalDataSource.getLastValidCredentials();
            if (connectivityManager.isDeviceOnline()) {
                final Credentials softLoginCredentials = new Credentials(
                        lastValidCredentials.getServerURL(),
                        lastValidCredentials.getUsername(),
                        softLoginPin);

                authenticationManager.login(softLoginCredentials,
                        new IAuthenticationManager.Callback<UserAccount>() {
                            @Override
                            public void onSuccess(UserAccount userAccount) {
                                credentialsLocalDataSource.saveLastValidCredentials(
                                        softLoginCredentials);
                                notifyLoginSuccess();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                if (throwable instanceof InvalidCredentialsException) {
                                    notifyInvalidPassword();
                                } else {
                                    notifyNetworkError();
                                }
                            }
                        });
            } else {
                verifyAgainstLastValidCredentials(lastValidCredentials);
            }

        } else {
            notifyMaxLoginAttemptsReached();
        }
    }

    private boolean isLoginEnable() {
        InvalidLoginAttempts invalidLoginAttempts =
                invalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();
        return invalidLoginAttempts.isLoginEnabled();
    }

    private void verifyAgainstLastValidCredentials(Credentials lastValidCredentials) {
        if (lastValidCredentials.getPassword().equals(softLoginPin)) {
            notifyLoginSuccess();
        } else {
            notifyNetworkError();
        }
    }

    //TODO: It's necessary here
    /*private void notifyOnServerPinChanged() {

        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onServerPinChanged();
            }
        });
    }*/

    public void notifyLoginSuccess() {
        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSoftLoginSuccess();
            }
        });
    }

    public void notifyInvalidPassword() {
        InvalidLoginAttempts invalidLoginAttempts =
                invalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();

        try {
            invalidLoginAttempts.addFailedAttempts();
        } catch (ActionNotAllowed actionNotAllowed) {
            actionNotAllowed.printStackTrace();
            notifyMaxLoginAttemptsReached();
        }

        invalidLoginAttemptsLocalDataSource.saveInvalidLoginAttempts(invalidLoginAttempts);

        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidPassword();
            }
        });

        if (!invalidLoginAttempts.isLoginEnabled()) {
            notifyMaxLoginAttemptsReached();
        }
    }

    private void notifyMaxLoginAttemptsReached() {
        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onMaxInvalidLoginAttemptsError();
            }
        });
    }

    public void notifyNetworkError() {
        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    public interface Callback {
        void onSoftLoginSuccess();

        void onInvalidPassword();

        void onNetworkError();

        void onMaxInvalidLoginAttemptsError();

        //TODO: It's necessary here
        //void onServerPinChanged();
    }
}
