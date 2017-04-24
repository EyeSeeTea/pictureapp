package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.network.ServerAPIController;

public class OrganisationUnitDataSource implements IOrganisationUnitRepository {
    @Override
    public OrganisationUnit getCurrentOrganisationUnit() {
        OrganisationUnit organisationUnit = ServerAPIController.getCurrentOrgUnit();

        return organisationUnit;
    }

    @Override
    public OrganisationUnit getCurrentLocalOrganisationUnit() {
        return ServerAPIController.getCurrentLocalOrganisationUnit();
    }

    @Override
    public void banLocalOrganisationUnit(boolean ban) {
        ServerAPIController.banOrgUnit(ban);
    }

    @Override
    public void saveOrganisationUnit(OrganisationUnit organisationUnit) {
        ServerAPIController.saveOrganisationUnit(organisationUnit);
    }


}
