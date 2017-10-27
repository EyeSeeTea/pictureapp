package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

import java.net.MalformedURLException;
import java.net.UnknownHostException;


public class LoginUseCase extends ALoginUseCase implements UseCase {
    private IAuthenticationManager mAuthenticationManager;
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    Credentials mCredentials;
    Callback mCallback;

    public LoginUseCase(IAuthenticationManager authenticationManager, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAuthenticationManager = authenticationManager;
    }

    @Override
    public void execute(final Credentials credentials, final Callback loginCallback) {
        mCredentials = credentials;
        mCallback = loginCallback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        if (mCredentials == null) {
            onErrorCallback(mCallback,
                    new IllegalArgumentException("Credentials could not be null"));
        } else {
            mAuthenticationManager.login(mCredentials,
                    new IAuthenticationManager.Callback<UserAccount>() {
                        @Override
                        public void onSuccess(UserAccount userAccount) {
                            if (!mCredentials.isDemoCredentials()) {
                                logoutAndHardcodedLogin(mCredentials, mCallback);
                            } else {
                                mCallback.onLoginSuccess();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            onErrorCallback(mCallback, throwable);
                        }
                    });
        }
    }

    private void logoutAndHardcodedLogin(final Credentials credentials,
            final Callback loginCallback) {
        mAuthenticationManager.logout(new IAuthenticationManager.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {

                hardcodedLogin(credentials, loginCallback);
            }

            @Override
            public void onError(Throwable throwable) {
                onErrorCallback(loginCallback, throwable);
            }
        });
    }

    private void hardcodedLogin(Credentials credentials, final Callback loginCallback) {
        mAuthenticationManager.hardcodedLogin(credentials.getServerURL(),
                new IAuthenticationManager.Callback<UserAccount>() {

                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        loginCallback.onLoginSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        onErrorCallback(loginCallback, throwable);
                    }
                });
    }

    private void onErrorCallback(final Callback callback, Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof MalformedURLException
                || throwable instanceof UnknownHostException) {
            onServerURLNotValid();
        } else if (throwable instanceof InvalidCredentialsException) {
            onInvalidCredentials();
        } else if (throwable instanceof NetworkException) {
            onNetworkError();
        } else if (throwable instanceof ConfigJsonIOException) {
            onConfigJsonInvalid();
        } else {
            onUnexpectedError();
        }

    }

    private void onUnexpectedError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onUnexpectedError();
            }
        });
    }

    private void onConfigJsonInvalid() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onConfigJsonInvalid();
            }
        });
    }

    private void onNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    private void onInvalidCredentials() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidCredentials();
            }
        });
    }

    private void onServerURLNotValid() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onServerURLNotValid();
            }
        });
    }

}