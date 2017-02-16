package org.eyeseetea.malariacare.data.authentication;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.remote.AuthenticationDhisSDKDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class AuthenticationManager implements IAuthenticationManager {
    IAuthenticationDataSource userAccountLocalDataSource;
    IAuthenticationDataSource userAccountRemoteDataSource;

    public AuthenticationManager(Context context) {

        userAccountLocalDataSource = new AuthenticationLocalDataSource(context);
        userAccountRemoteDataSource = new AuthenticationDhisSDKDataSource(context);
    }

    @Override
    public void login(final Credentials credentials,
            final IAuthenticationManager.Callback<UserAccount> callback) {
        if (credentials.isDemoCredentials()) {
            localLogin(credentials, callback);
        } else {
            remoteLogin(credentials, callback);
        }
    }

    @Override
    public void login(String url, Callback<UserAccount> callback) {
        remoteLogin(getServerCredentials(url), callback);

    }

    @Override
    public void logout(final IAuthenticationManager.Callback<Void> callback) {

        //TODO: jsanchez fix find out IsDemo from current UserAccount getting from DataSource
        Credentials credentials = Session.getCredentials();

        if (credentials.isDemoCredentials()) {
            localLogout(callback);
        } else {
            remoteLogout(callback);
        }
    }

    private void remoteLogout(final IAuthenticationManager.Callback<Void> callback) {
        userAccountRemoteDataSource.logout(new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                localLogout(callback);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void remoteLogin(final Credentials credentials,
            final IAuthenticationManager.Callback<UserAccount> callback) {
        userAccountRemoteDataSource.login(credentials, new IDataSourceCallback<UserAccount>() {
            @Override
            public void onSuccess(UserAccount result) {
                localLogin(credentials, callback);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void localLogout(final IAuthenticationManager.Callback<Void> callback) {
        userAccountLocalDataSource.logout(new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void localLogin(Credentials credentials,
            final IAuthenticationManager.Callback<UserAccount> callback) {
        userAccountLocalDataSource.login(credentials, new IDataSourceCallback<UserAccount>() {
            @Override
            public void onSuccess(UserAccount userAccount) {
                callback.onSuccess(userAccount);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }


    public Credentials getServerCredentials(String serverUrl) {

        String username = PreferencesState.getInstance().getContext().getString(
                R.string.user_push);
        String password = PreferencesState.getInstance().getContext().getString(R.string.pass_push);

        Credentials credentials = new Credentials(serverUrl, username, password);
        return credentials;
    }
}
