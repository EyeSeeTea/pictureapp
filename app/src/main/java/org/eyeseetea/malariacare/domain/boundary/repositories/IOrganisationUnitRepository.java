package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;

public interface IOrganisationUnitRepository {
    OrganisationUnit getCurrentOrganisationUnit();
    OrganisationUnit getCurrentLocalOrganisationUnit();
    void banLocalOrganisationUnit(boolean ban);

    void saveOrganisationUnit(OrganisationUnit organisationUnit);
}
