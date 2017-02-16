package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class AuthenticationLocalDataSource implements IAuthenticationDataSource {

    Context mContext;

    public AuthenticationLocalDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        User user = User.getLoggedUser();

        if (user != null) {
            user.delete();
        }

        clearCredentials();

        Session.logout();

        PopulateDB.wipeDatabase();

        callback.onSuccess(null);
    }

    @Override
    public void login(Credentials credentials, IDataSourceCallback<UserAccount> callback) {
        User user = new User(credentials.getUsername(), credentials.getUsername());

        User.insertLoggedUser(user);

        Session.setUser(user);
        Session.setCredentials(credentials);

        saveCredentials(credentials);

        callback.onSuccess(null);
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


    private void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                mContext.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }
}
