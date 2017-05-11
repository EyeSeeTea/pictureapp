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
    OrganisationUnit getCurrentOrganisationUnit() throws NetworkException, ApiCallException;

    OrganisationUnit getUserOrgUnit(Credentials credentials)
            throws PullConversionException, NetworkException, IOException, JSONException,
            ConfigJsonIOException;

}
