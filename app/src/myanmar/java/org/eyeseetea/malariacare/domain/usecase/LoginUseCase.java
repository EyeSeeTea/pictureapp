package org.eyeseetea.malariacare.domain.usecase;

import static org.eyeseetea.malariacare.R.string.server;
import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

import android.content.Context;
import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.PullController;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginUseCase extends ALoginUseCase {

    public LoginUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute(Credentials credentials, Callback callback) {
        if (credentials.isDemoCredentials()) {
            executeLocalLogin(credentials);
            createDummyDataInDB(context);
            callback.onLoginSuccess();
        }
        else{
            executeDhisLogin(credentials, callback);
        }
    }

    private void executeDhisLogin(final Credentials credentials, final Callback callback) {
        Configuration configuration = new Configuration(credentials.getServerURL());

        D2.configure(configuration)
                .flatMap(new Func1<Void, Observable<UserAccount>>() {
                    @Override
                    public Observable<UserAccount> call(Void aVoid) {
                        return D2.me().signIn(credentials.getUsername(), credentials.getPassword());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserAccount>() {
                    @Override
                    public void call(UserAccount userAccount) {
                        executeLocalLogin(credentials);
                        callback.onLoginSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onLoginError(throwable.getMessage());
                    }
                });
    }

/*    public void executeDhisLogin(final Credentials credentials, final Callback callback) {
        String serverUrl = credentials.getServerURL();
        if (!isEmpty(serverUrl)) {
            // configure D2
            Configuration configuration = new Configuration(serverUrl);
            D2.configure(configuration).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void v) {
                            validateCredentials(credentials, callback);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            callback.onLoginError(throwable.getMessage());
                        }
                    });
        }
    }

    *//**
     * login in the dhis server and launch the onSuccess method
     *//*
    public void validateCredentials(final Credentials credentials, final Callback callback) {

        D2.me().signIn(credentials.getUsername(), credentials.getPassword()).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Action1<UserAccount>() {
                    @Override
                    public void call(
                            UserAccount userAccount) {
                        executeLocalLogin(credentials);
                        callback.onLoginSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onLoginError(throwable.getMessage());
                    }
                });
    }*/

    private void executeLocalLogin(Credentials credentials) {
        User user = new User(credentials.getUsername(), credentials.getUsername());

        User.insertLoggedUser(user);

        Session.setUser(user);
        Session.setCredentials(credentials);

        saveCredentials(credentials);
    }

    private void saveCredentials(Credentials credentials) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                credentials.getServerURL());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user,
                credentials.getUsername());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password,
                credentials.getPassword());
        PreferencesState.getInstance().reloadPreferences();
    }

    private void createDummyDataInDB(Context context) {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(context.getAssets());
                PullController.convertOUinOptions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isLogoutNeeded(Credentials credentials) {
        return false;
    }
}
