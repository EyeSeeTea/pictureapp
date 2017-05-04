package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrgUnit;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;

public interface IOrgUnitRepository {
    OrgUnit getUserOrgUnit(Credentials credentials) throws PullConversionException,
            NetworkException;
}
