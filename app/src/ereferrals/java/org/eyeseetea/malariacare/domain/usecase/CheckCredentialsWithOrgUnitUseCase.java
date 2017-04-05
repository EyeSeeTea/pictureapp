package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class CheckCredentialsWithOrgUnitUseCase implements UseCase {
    public interface Callback {
        void onCorrectCredentials();

        void onBadCredentials();
    }

    private Credentials mCredentials;
    private Callback mCallback;

    public void execute(Credentials credentials, Callback callback) {
        mCredentials = credentials;
        mCallback = callback;
        run();
    }

    @Override
    public void run() {
        Credentials savedCredentials = PreferencesEReferral.getUserCredentialsFromPreferences();
        if (savedCredentials.getUsername().equals(mCredentials.getUsername())
                && savedCredentials.getPassword().equals(mCredentials.getPassword())) {
            mCallback.onCorrectCredentials();
        } else {
            mCallback.onBadCredentials();
        }
    }
}
