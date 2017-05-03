package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PullOrganisationCredentials {

    public void pullOrganisationCredentialsProgram(Credentials credentials,
            final IDataSourceCallback<Program> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();
        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            try {
            JSONObject jsonObject = ServerAPIController.getOrganisationUnitsByCode(
                    credentials.getUsername());

            Program program = null;

                program = parseOrganisationUnitToCorrectProgram(jsonObject);
                callback.onSuccess(program);
            } catch (PullConversionException e) {
                e.printStackTrace();
                callback.onError(e);
            }

        }
    }

    private Program parseOrganisationUnitToCorrectProgram(JSONObject organisationUnit)
            throws PullConversionException {
        try {
            JSONArray ancestors = organisationUnit.getJSONArray("ancestors");
            for (int i = 0; i < ancestors.length(); i++) {
                if (ancestors.getJSONObject(i).getInt("level") == Integer.parseInt(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.ancestor_level))) {
                    return Program.findByName(ancestors.getJSONObject(i).getString("code"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PullConversionException();
        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
