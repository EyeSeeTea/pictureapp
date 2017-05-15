package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;


public class CredentialsLocalDataSource implements ICredentialsRepository {
    @Override
    public Credentials getOrganisationCredentials() {
        return PreferencesEReferral.getUserCredentialsFromPreferences();
    }

    @Override
    public void saveOrganisationCredentials(Credentials credentials) {
        PreferencesEReferral.saveLoggedUserCredentials(credentials);
    }

    @Override
    public Credentials getCredentials() {
        return PreferencesState.getCredentialsFromPreferences();
    }
}
