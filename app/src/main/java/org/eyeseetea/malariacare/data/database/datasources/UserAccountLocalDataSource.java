package org.eyeseetea.malariacare.data.database.datasources;

import static org.eyeseetea.malariacare.data.database.utils.Session.getCredentials;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.IUserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class UserAccountLocalDataSource implements IUserAccountDataSource {

    Context mContext;

    public UserAccountLocalDataSource (Context context){
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

/*    @Override
    public void getCurrentUserAccount(IDataSourceCallback<UserAccount> callback) {
        Credentials credentials = Session.getCredentials();
        UserAccount userAccount = new UserAccount(credentials.getUsername(), credentials.isDemoCredentials());

        callback.onSuccess(userAccount);
    }*/

    private void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                mContext.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }
}
