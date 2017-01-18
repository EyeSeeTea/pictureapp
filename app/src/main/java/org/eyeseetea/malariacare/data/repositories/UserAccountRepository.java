package org.eyeseetea.malariacare.data.repositories;

import static com.raizlabs.android.dbflow.config.FlowLog.Level.I;

import android.content.Context;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.IUserAccountDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.remote.UserAccountDhisSDKDataSource;
import org.eyeseetea.malariacare.domain.boundary.IRepositoryCallback;
import org.eyeseetea.malariacare.domain.boundary.IUserAccountRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class UserAccountRepository implements IUserAccountRepository {
    IUserAccountDataSource userAccountLocalDataSource;
    IUserAccountDataSource userAccountRemoteDataSource;

    public UserAccountRepository(Context context) {

        userAccountLocalDataSource = new UserAccountLocalDataSource(context);
        userAccountRemoteDataSource = new UserAccountDhisSDKDataSource();
    }

    @Override
    public void login(final Credentials credentials, final IRepositoryCallback<UserAccount> callback) {
        if (credentials.isDemoCredentials())
            localLogin(credentials, callback);
        else
            remoteLogin(credentials, callback);
    }

    @Override
    public void logout(final IRepositoryCallback<Void> callback) {

        //TODO: jsanchez fix find out IsDemo from current UserAccount getting from DataSource
        Credentials credentials = Session.getCredentials();

        if (credentials.isDemoCredentials())
            localLogout(callback);
        else
            remoteLogout(callback);
    }

    private void remoteLogout(final IRepositoryCallback<Void> callback) {
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
            final IRepositoryCallback<UserAccount> callback) {
        userAccountRemoteDataSource.login(credentials, new IDataSourceCallback<UserAccount>() {
            @Override
            public void onSuccess(UserAccount result) {
                localLogin(credentials,callback);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void localLogout(final IRepositoryCallback<Void> callback){
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

    private void localLogin(Credentials credentials,final IRepositoryCallback<UserAccount> callback){
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
}
