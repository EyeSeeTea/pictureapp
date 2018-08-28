package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;

public interface ICredentialsRepository {

    Credentials getLastValidCredentials();

    void saveLastValidCredentials(Credentials credentials);

    void clearLastValidCredentials();

    Credentials getCredentials();
}
