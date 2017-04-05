package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.hisp.dhis.client.sdk.models.attribute.AttributeValue;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.json.JSONObject;

import java.io.IOException;

public class PullOrganisationCredentials {

    public void pullOrganisationCredentials(Credentials credentials,
            final IDataSourceCallback<Credentials> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            JSONObject jsonObject = ServerAPIController.getOrganisationUnitsByCode(
                    credentials.getUsername());
            try {
                Credentials OUCredentials = parseOrganisationUnitToCredentials(jsonObject);
                callback.onSuccess(OUCredentials);
            } catch (PullConversionException e) {
                callback.onError(e);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private Credentials parseOrganisationUnitToCredentials(JSONObject organisationUnits)
            throws PullConversionException {


        if (organisationUnits != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                OrganisationUnit organisationUnit = mapper.readValue(organisationUnits.toString(),
                        OrganisationUnit.class);
                organisationUnit.toString();
                String username = organisationUnit.getCode();
                String password = null;
                for (AttributeValue attributeValue : organisationUnit.getAttributeValues()) {
                    if (attributeValue.getAttribute().getCode().equals(
                            PreferencesState.getInstance().getContext().getString(
                                    R.string.attribute_pin_code))) {
                        password = attributeValue.getValue();
                    }
                }
                return new Credentials("", username, password);

            } catch (IOException e) {
                e.printStackTrace();
                throw new PullConversionException();
            }
        }
        return null;
    }

}
