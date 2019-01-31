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
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class LoginUseCase extends ALoginUseCase implements UseCase {
    private final IConnectivityManager mConnectivityManager;
    private final IAuthenticationManager mAuthenticationManager;
    private final IMainExecutor mMainExecutor;
    private final IAsyncExecutor mAsyncExecutor;
    private final ICredentialsRepository mCredentialsLocalDataSource;
    private final IInvalidLoginAttemptsRepository mInvalidLoginAttemptsLocalDataSource;

    private Credentials insertedCredentials;
    private Callback mCallback;

    public LoginUseCase(IConnectivityManager connectivityManager,
            IAuthenticationManager authenticationManager, IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor, ICredentialsRepository credentialsLocalDataSource,
            IInvalidLoginAttemptsRepository iInvalidLoginAttemptsRepository) {
        mConnectivityManager = connectivityManager;
        mAuthenticationManager = authenticationManager;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mCredentialsLocalDataSource = credentialsLocalDataSource;
        mInvalidLoginAttemptsLocalDataSource = iInvalidLoginAttemptsRepository;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        mCallback = callback;
        insertedCredentials = credentials;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        if (insertedCredentials.isDemoCredentials()) {
            runDemoLogin();
        }else {
            if (isLoginEnable()) {
                if (mConnectivityManager.isDeviceOnline()) {
                    mAuthenticationManager.login(insertedCredentials,
                            new IAuthenticationManager.Callback<UserAccount>() {
                                @Override
                                public void onSuccess(UserAccount userAccount) {
                                    mCredentialsLocalDataSource.saveLastValidCredentials(
                                            insertedCredentials);
                                    notifyLoginSuccess();
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    if (throwable instanceof MalformedURLException
                                            || throwable instanceof UnknownHostException) {
                                        notifyServerURLNotValid();
                                    } else if (throwable instanceof InvalidCredentialsException) {
                                        notifyInvalidCredentials();
                                    } else if (throwable instanceof AvailableApiException) {
                                        notifyServerNotAvailable(throwable.getMessage());
                                    } else {
                                        throwable.printStackTrace();
                                    }
                                }
                            });
                } else {
                    notifyNetworkError();
                }

            } else {
                notifyMaxLoginAttemptsReached();
            }
        }
    }

    private void runDemoLogin() {
        mAuthenticationManager.login(insertedCredentials,
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        mCredentialsLocalDataSource.saveLastValidCredentials(insertedCredentials);
                        notifyLoginSuccess();
                        }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof MalformedURLException
                                || throwable instanceof UnknownHostException) {
                            notifyServerURLNotValid();
                        } else if (throwable instanceof InvalidCredentialsException) {
                            notifyInvalidCredentials();
                        } else if (throwable instanceof NetworkException) {
                            notifyNetworkError();
                        }
                    }
                });
    }

    private boolean isLoginEnable() {
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();
        return invalidLoginAttempts.isLoginEnabled();
    }

    public void notifyLoginSuccess() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onLoginSuccess();
            }
        });
    }

    public void notifyInvalidCredentials() {
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();

        try {
            invalidLoginAttempts.addFailedAttempts();
        } catch (ActionNotAllowed actionNotAllowed) {
            actionNotAllowed.printStackTrace();
            notifyMaxLoginAttemptsReached();
        }

        mInvalidLoginAttemptsLocalDataSource.saveInvalidLoginAttempts(invalidLoginAttempts);

        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidCredentials();
            }
        });

        if (!invalidLoginAttempts.isLoginEnabled()) {
            notifyMaxLoginAttemptsReached();
        }
    }

    private void notifyMaxLoginAttemptsReached() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onMaxLoginAttemptsReachedError();
            }
        });
    }

    public void notifyNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    public void notifyServerNotAvailable(final String message) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onServerNotAvailable(message);
            }
        });
    }

    public void notifyServerURLNotValid() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onServerURLNotValid();
            }
        });
    }
}
