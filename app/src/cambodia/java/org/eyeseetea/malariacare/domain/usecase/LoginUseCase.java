package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonNotPresentException;
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
    public void execute(final Credentials credentials, final Callback loginCallback) {
        mAuthenticationManager.login(credentials,
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        if (!credentials.isDemoCredentials()) {
                            logoutAndHardcodedLogin(credentials, loginCallback);
                        } else {
                            loginCallback.onLoginSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        onErrorCallback(loginCallback, throwable);
                    }
                });
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
        if (throwable instanceof MalformedURLException
                || throwable instanceof UnknownHostException) {
            callback.onServerURLNotValid();
        } else if (throwable instanceof InvalidCredentialsException) {
            callback.onInvalidCredentials();
        } else if (throwable instanceof NetworkException) {
            callback.onNetworkError();
        } else if (throwable instanceof ConfigJsonNotPresentException) {
            callback.onConfigJsonNotPresent();
        }
    }
}