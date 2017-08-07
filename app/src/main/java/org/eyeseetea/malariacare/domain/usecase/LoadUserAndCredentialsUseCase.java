package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class LoadUserAndCredentialsUseCase {

    Context mContext;

    public LoadUserAndCredentialsUseCase(Context context) {
        mContext = context;
    }

    public void execute() {
        loadUser();
        loadCredentials();
    }

    private void loadUser() {
        Session.setUserDB(UserDB.getLoggedUser());
    }

    private void loadCredentials() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                mContext);

        String serverURL = sharedPreferences.getString(mContext.getString(R.string.dhis_url), "");
        String username = sharedPreferences.getString(mContext.getString(R.string.dhis_user), "");
        String password = sharedPreferences.getString(mContext.getString(R.string.dhis_password),
                "");

        Credentials credentials = new Credentials(serverURL, username, password);

        Session.setCredentials(credentials);
    }
}
