package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;


public class CredentialsDataSource implements ICredentialsRepository {


    @Override
    public Credentials getOrganisationCredentials(Credentials credentials)
            throws PullConversionException, NetworkException {
        return ServerAPIController.getOrgUnitCredentials(credentials);
    }

    @Override
    public void saveOrganisationCredentials(Credentials credentials) {

    }

    @Override
    public Credentials getCredentials() {
        return null;
    }
}
