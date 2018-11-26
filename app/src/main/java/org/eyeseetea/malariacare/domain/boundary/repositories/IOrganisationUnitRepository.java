package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.organisationunit.ExistsMoreThanOneOrgUnitByPhoneException;

public interface IOrganisationUnitRepository {

    void removeCurrentOrganisationUnit();

    interface BanOrgUnitChangeListener{
        void onBanOrgUnitChanged(OrganisationUnit organisationUnit);
    }

    OrganisationUnit getCurrentOrganisationUnit(ReadPolicy readPolicy)
            throws NetworkException, ApiCallException;

    OrganisationUnit getUserOrgUnit(Credentials credentials)
            throws NetworkException, ApiCallException;


    void saveOrganisationUnit(OrganisationUnit organisationUnit);

    void setBanOrgUnitChangeListener(BanOrgUnitChangeListener listener);

    OrganisationUnit getOrganisationUnitByPhone(Device device)
            throws ApiCallException, ExistsMoreThanOneOrgUnitByPhoneException;

    void saveCurrentOrganisationUnit(OrganisationUnit organisationUnit);

    void saveCurrentProgram(Program program);

    Program getOrganisationUnitGroupFromRemote(OrganisationUnit organisationUnit)
            throws NetworkException, ApiCallException;

}
