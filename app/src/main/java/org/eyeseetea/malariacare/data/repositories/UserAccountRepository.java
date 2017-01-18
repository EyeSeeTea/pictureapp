package org.eyeseetea.malariacare.data.repositories;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.IUserAccountRepository;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UserAccountRepository implements IUserAccountRepository {
    private Context mContext;

    public UserAccountRepository(Context context){
        mContext = context;
    }

    @Override
    public void removeCurrentUserAccount(RemoveCurrentUserAccountCallback callback) {
        User loggedUser = User.getLoggedUser();

        if (Session.getCredentials().isDemoCredentials()) {
            executeLocalLogout(loggedUser);
            callback.onSuccess();
        } else {
            executeDhisLogout(loggedUser, callback);
        }
    }

    private void executeDhisLogout(final User user, final RemoveCurrentUserAccountCallback callback) {
        Configuration configuration = new Configuration(
                PreferencesState.getInstance().getDhisURL());

        D2.configure(configuration)
                .flatMap(new Func1<Void, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Void aVoid) {
                        return D2.me().signOut();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        executeLocalLogout(user);
                        callback.onSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    private void executeLocalLogout(User user) {
        if (user != null) {
            user.delete();
        }

        clearCredentials();

        Session.logout();

        PopulateDB.wipeDatabase();
    }

    private void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                mContext.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }
}
