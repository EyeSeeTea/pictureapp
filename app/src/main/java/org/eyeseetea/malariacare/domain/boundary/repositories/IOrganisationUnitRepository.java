package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.json.JSONException;

import java.io.IOException;

public interface IOrganisationUnitRepository {

    interface BanOrgUnitChangeListener{
        void onBanOrgUnitChanged(OrganisationUnit organisationUnit);
    }

    OrganisationUnit getCurrentOrganisationUnit(ReadPolicy readPolicy)
            throws NetworkException, ApiCallException;

    OrganisationUnit getUserOrgUnit(Credentials credentials)
            throws NetworkException, ApiCallException;


    void saveOrganisationUnit(OrganisationUnit organisationUnit);

    void setBanOrgUnitChangeListener(BanOrgUnitChangeListener listener);

}
