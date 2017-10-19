package org.eyeseetea.malariacare.data.database.datasources.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesCNM;


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
}
