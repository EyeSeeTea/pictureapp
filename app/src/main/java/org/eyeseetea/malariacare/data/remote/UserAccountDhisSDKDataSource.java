package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.IUserAccountDataSource;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UserAccountDhisSDKDataSource implements IUserAccountDataSource {
    @Override
    public void logout(final IDataSourceCallback<Void> callback) {
        D2.me().signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        callback.onSuccess(null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    @Override
    public void login(final Credentials credentials,
            final IDataSourceCallback<UserAccount> callback) {
        Configuration configuration = new Configuration(credentials.getServerURL());

        D2.configure(configuration)
                .flatMap(
                        new Func1<Void, Observable<org.hisp.dhis.client.sdk.models.user
                                .UserAccount>>() {
                            @Override
                            public Observable<org.hisp.dhis.client.sdk.models.user.UserAccount>
                            call(
                                    Void aVoid) {
                                return D2.me().signIn(credentials.getUsername(),
                                        credentials.getPassword());
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<org.hisp.dhis.client.sdk.models.user.UserAccount>() {
                    @Override
                    public void call(
                            org.hisp.dhis.client.sdk.models.user.UserAccount dhisUserAccount) {
                        UserAccount userAccount = new UserAccount(credentials.getUsername(),
                                credentials.isDemoCredentials());
                        callback.onSuccess(userAccount);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }
}
