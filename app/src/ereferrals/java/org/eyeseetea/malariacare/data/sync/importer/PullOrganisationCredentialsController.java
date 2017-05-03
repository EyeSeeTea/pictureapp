package org.eyeseetea.malariacare.data.sync.importer;

import android.content.Context;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.remote.PullOrganisationCredentials;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class PullOrganisationCredentialsController {

    public interface Callback {
        void onComplete();

        void onError(Throwable throwable);
    }

    private static String TAG = "PullOrganisationCredentialsController";
    private Credentials mCredentials;
    private Context mContext;
    private PullOrganisationCredentials
            mPullOrganisationCredentials = new PullOrganisationCredentials();


    public PullOrganisationCredentialsController(
            Credentials credentials, Context context) {
        mCredentials = credentials;
        mContext = context;
    }

    public void pullUserProgram(final Callback callback) {
        mPullOrganisationCredentials.pullOrganisationCredentialsProgram(
                PreferencesEReferral.getUserCredentialsFromPreferences(),
                new IDataSourceCallback<Program>() {
                    @Override
                    public void onSuccess(Program result) {
                        saveUserProgram(result);
                        callback.onComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    private void saveUserProgram(Program program) {
        PreferencesEReferral.saveUserProgramId(program.getId_program());
    }

}
