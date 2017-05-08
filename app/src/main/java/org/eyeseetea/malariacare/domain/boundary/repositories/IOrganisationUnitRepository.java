package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public interface IOrganisationUnitRepository {
    OrganisationUnit getCurrentOrganisationUnit() throws NetworkException, ApiCallException;
}
