package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;

public interface ICredentialsRepository {

    Credentials getOrganisationCredentials();

    void saveOrganisationCredentials(Credentials credentials);

    void clearOrganisationCredentials();

    Credentials getCredentials();
}
