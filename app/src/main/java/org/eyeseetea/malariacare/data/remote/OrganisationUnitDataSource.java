package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.json.JSONException;

import java.io.IOException;

public class OrganisationUnitDataSource implements IOrganisationUnitRepository {
    @Override
    public OrganisationUnit getCurrentOrganisationUnit() throws NetworkException, ApiCallException {

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

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
