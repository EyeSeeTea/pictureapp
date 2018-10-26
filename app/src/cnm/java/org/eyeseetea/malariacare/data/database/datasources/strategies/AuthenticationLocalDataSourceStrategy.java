package org.eyeseetea.malariacare.data.database.datasources.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesCNM;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;


public class AuthenticationLocalDataSourceStrategy extends AAuthenticationLocalDataSourceStrategy {
    public AuthenticationLocalDataSourceStrategy(
            AuthenticationLocalDataSource authenticationLocalDataSource) {
        super(authenticationLocalDataSource);
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        super.logout(callback);
        PreferencesCNM.setMetadataDownloaded(false);
    }

    @Override
    public void clearCredentials(Context context) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }
}
