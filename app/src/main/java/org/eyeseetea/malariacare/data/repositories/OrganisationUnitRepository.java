package org.eyeseetea.malariacare.data.repositories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.network.ServerAPIController;

public class OrganisationUnitRepository implements IOrganisationUnitRepository {
    BanOrgUnitChangeListener mBanOrgUnitChangeListener;

    @Override
    public OrganisationUnit getCurrentOrganisationUnit(ReadPolicy readPolicy)
            throws NetworkException, ApiCallException {

        OrganisationUnit organisationUnit;

        if (readPolicy == ReadPolicy.REMOTE) {
            organisationUnit = getFromRemote();

            verifyBanChanged(organisationUnit);

            OrgUnitDB.refresh(organisationUnit);
        } else {
            organisationUnit = OrgUnitDB.getByName(PreferencesState.getInstance().getOrgUnit());
        }

        return organisationUnit;
    }

    private void verifyBanChanged(OrganisationUnit organisationUnit) {
        OrgUnitDB cachedOrganisationUnit =
                OrgUnitDB.findByName(PreferencesState.getInstance().getOrgUnit());

        if (cachedOrganisationUnit != null
                && organisationUnit.isBanned() != cachedOrganisationUnit.isBanned()) {
            mBanOrgUnitChangeListener.onBanOrgUnitChanged(organisationUnit);
        }
    }

    private OrganisationUnit getFromRemote() throws NetworkException, ApiCallException {
        if (!isNetworkAvailable()) {
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

        OrgUnitDB.refresh(organisationUnit);
    }


    @Override
    public OrganisationUnit getUserOrgUnit(Credentials credentials)
            throws ApiCallException {
        return ServerAPIController.getOrganisationUnitsByCode(credentials.getUsername());
    }


    @Override
    public void setBanOrgUnitChangeListener(
            BanOrgUnitChangeListener listener) {
        mBanOrgUnitChangeListener = listener;
    }

    @Override
    public OrganisationUnit getOrganisationUnitByPhone(Device device) throws ApiCallException {
        String IMEI = device.getIMEI();
        if (IMEI == null || IMEI.isEmpty()) {
            return null;
        }
        OrganisationUnit organisationUnit = ServerAPIController.getOrgUnitByPhone(IMEI);
        return organisationUnit;
    }

    @Override
    public void saveCurrentOrganisationUnit(OrganisationUnit organisationUnit) {
        if (OrgUnitDB.getByName(organisationUnit.getName()) == null) {
            OrgUnitDB orgUnitDB = new OrgUnitDB(organisationUnit.getUid(),
                    organisationUnit.getName(), null, null);
            orgUnitDB.save();
        }
        PreferencesState.getInstance().setOrgUnit(organisationUnit.getName());
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void removeCurrentOrganisationUnit() {
        if (OrgUnitDB.getByName(PreferencesState.getInstance().getOrgUnit()) != null) {
            OrgUnitDB orgUnitDB = OrgUnitDB.findByName(PreferencesState.getInstance().getOrgUnit());
            orgUnitDB.delete();
        }
        PreferencesState.getInstance().setOrgUnit(null);
    }

}
