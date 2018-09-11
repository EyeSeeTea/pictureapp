package org.eyeseetea.malariacare.data.remote;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.remote.model.AuthResponse;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;

public class ProgramWSDataSource {

    private static String TAG = "ProgramWSDataSource";

    private final eReferralsAPIClient mEReferralsAPIClient;

    public ProgramWSDataSource() {
        mEReferralsAPIClient = new eReferralsAPIClient(PreferencesEReferral.getWSURL());
    }

    public Program getUserProgram() {
        Program program = null;
        Credentials credentials = PreferencesEReferral.getUserCredentialsFromPreferences();

        try {
            AuthResponse response = mEReferralsAPIClient.auth(credentials.getUsername(),
                    credentials.getPassword());

            program = new Program(response.getCountry().getCode(), response.getCountry().getId());

        } catch (Exception e) {
            Log.e(TAG, "An error has occurred retrieved user program: " + e.getMessage());
        }

        return program;
    }
}
