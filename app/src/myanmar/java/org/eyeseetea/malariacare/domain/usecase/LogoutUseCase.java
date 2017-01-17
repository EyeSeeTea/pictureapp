package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LogoutUseCase extends ALogoutUseCase {
    public LogoutUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute(Callback callback) {
        User loggedUser = User.getLoggedUser();

        if (Session.getCredentials().isDemoCredentials()) {
            executeLocalLogout(loggedUser);
            callback.onLogoutSuccess();
        }
        else
            executeDhisLogout(loggedUser,callback);
    }

    private void executeDhisLogout(final User user, final Callback callback){
        Configuration configuration = new Configuration(PreferencesState.getInstance().getDhisURL());

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
                        callback.onLogoutSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onLogoutError(throwable.getMessage());
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
                context.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }


}
