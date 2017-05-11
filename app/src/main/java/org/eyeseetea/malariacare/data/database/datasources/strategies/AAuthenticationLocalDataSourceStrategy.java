package org.eyeseetea.malariacare.data.database.datasources.strategies;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;

public abstract class AAuthenticationLocalDataSourceStrategy {
    protected AuthenticationLocalDataSource mAuthenticationLocalDataSource;

    public AAuthenticationLocalDataSourceStrategy(
            AuthenticationLocalDataSource authenticationLocalDataSource) {
        mAuthenticationLocalDataSource = authenticationLocalDataSource;
    }

    public void logout(IDataSourceCallback<Void> callback) {
        User user = User.getLoggedUser();

        if (user != null) {
            user.delete();
        }

        mAuthenticationLocalDataSource.clearCredentials();

        Session.logout();

        //reset org_unit
        PreferencesState.getInstance().saveStringPreference(R.string.org_unit,
                "");

        new PopulateDBStrategy().logoutWipe();

        callback.onSuccess(null);
    }
}
