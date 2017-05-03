package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;

public interface ICredentialsRepository {

    Credentials getOrganisationCredentials(Credentials credentials)
            throws PullConversionException, NetworkException;

    void saveOrganisationCredentials(Credentials credentials);
}
