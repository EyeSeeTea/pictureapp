package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.domain.boundary.repositories.IOrgUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrgUnit;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;

public class OrgUnitDataSource implements IOrgUnitRepository {
    @Override
    public OrgUnit getUserOrgUnit(Credentials credentials)
            throws PullConversionException, NetworkException {
        return ServerAPIController.getOrganisationUnitsByCode(credentials.getUsername());
    }
}
