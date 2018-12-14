package org.eyeseetea.malariacare.data.database.datasources.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;


public class AuthenticationLocalDataSourceStrategy extends AAuthenticationLocalDataSourceStrategy {
    public AuthenticationLocalDataSourceStrategy(
            AuthenticationLocalDataSource authenticationLocalDataSource) {
        super(authenticationLocalDataSource);
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        UserDB userDB = UserDB.getLoggedUser();

        if (userDB != null) {
            userDB.delete();
        }

        mAuthenticationLocalDataSource.clearCredentials();
        ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.clearLastValidCredentials();

        Session.logout();

        //reset org_unit
        PreferencesState.getInstance().saveStringPreference(R.string.org_unit,
                "");

        new PopulateDBStrategy().logoutWipe();

        callback.onSuccess(null);
    }

    @Override
    public String getServerDefaultUrl(Context mContext) {
        return mContext.getString(R.string.ws_base_url);
    }
}
