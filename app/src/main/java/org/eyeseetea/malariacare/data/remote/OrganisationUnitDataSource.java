package org.eyeseetea.malariacare.data.remote;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;
import org.json.JSONException;

import java.io.IOException;

public class OrganisationUnitDataSource implements IOrganisationUnitRepository {
    BanOrgUnitChangeListener mBanOrgUnitChangeListener;

    @Override
    public OrganisationUnit getCurrentOrganisationUnit(ReadPolicy readPolicy)
            throws NetworkException, ApiCallException {

        OrganisationUnit organisationUnit = getFromRemote();

        OrgUnit orgUnit = verifyBanChanged(organisationUnit);

        if (orgUnit != null && !orgUnit.isBanned()) {
            OrgUnit.refresh(organisationUnit);
        }

        return organisationUnit;
    }

    @Nullable
    private OrgUnit verifyBanChanged(OrganisationUnit organisationUnit) {
        OrgUnit orgUnit = OrgUnit.findByName(PreferencesState.getInstance().getOrgUnit());

        if (orgUnit != null && organisationUnit.isBanned() != orgUnit.isBanned()) {
            mBanOrgUnitChangeListener.onBanOrgUnitChanged(organisationUnit);
        }
        return orgUnit;
    }

    private OrganisationUnit getFromRemote() throws NetworkException, ApiCallException {
        if (ConnectivityStatus.isConnected(PreferencesState.getInstance().getContext())) {
            throw new NetworkException();
        }

        OrganisationUnit organisationUnit = null;
        try {
            organisationUnit = ServerAPIController.getCurrentOrgUnit();
        } catch (Exception e) {
            throw new ApiCallException("Error checking banned call");
        }
        return organisationUnit;
    }

    @Override
    public void saveOrganisationUnit(OrganisationUnit organisationUnit) {
        ServerAPIController.saveOrganisationUnit(organisationUnit);

        OrgUnit.refresh(organisationUnit);
    }


    @Override
    public OrganisationUnit getUserOrgUnit(Credentials credentials)
            throws PullConversionException, NetworkException, IOException, JSONException,
            ConfigJsonIOException {
        return ServerAPIController.getOrganisationUnitsByCode(credentials.getUsername());
    }


    @Override
    public void setBanOrgUnitChangeListener(
            BanOrgUnitChangeListener listener) {
        mBanOrgUnitChangeListener = listener;
    }
}
