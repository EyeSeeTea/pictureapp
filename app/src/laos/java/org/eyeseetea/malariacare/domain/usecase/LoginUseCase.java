package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class LoginUseCase extends ALoginUseCase {
    private IAuthenticationManager mAuthenticationManager;

    public LoginUseCase(IAuthenticationManager authenticationManager) {
        mAuthenticationManager = authenticationManager;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        mAuthenticationManager.login(credentials,
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        mAuthenticationManager.logout(new IAuthenticationManager.Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {

                                mAuthenticationManager.login(credentials.getServerURL(),
                                        new IAuthenticationManager.Callback<UserAccount>() {

                                            @Override
                                            public void onSuccess(UserAccount userAccount) {
                                                callback.onLoginSuccess();
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                onErrorCallback(callback, throwable);
                                            }
                                        });
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                onErrorCallback(callback, throwable);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        onErrorCallback(callback, throwable);
                    }
                });
    }

    private void onErrorCallback(final Callback callback, Throwable throwable) {
        if (throwable instanceof MalformedURLException
                || throwable instanceof UnknownHostException) {
            callback.onServerURLNotValid();
        } else if (throwable instanceof InvalidCredentialsException) {
            callback.onInvalidCredentials();
        } else if (throwable instanceof NetworkException) {
            callback.onNetworkError();
        }
    }
}
