package org.eyeseetea.malariacare.domain.usecase;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class CheckCredentialsWithOrgUnitUseCase implements UseCase {
    @Override
    public void run() {
        Credentials savedCredentials = PreferencesEReferral.getUserCredentialsFromPreferences();
        if (savedCredentials.getUsername().equals(mCredentials.getUsername())
                && savedCredentials.getPassword().equals(mCredentials.getPassword())) {
            mCallback.onCorrectCredentials();
            PreferencesEReferral.resetBadLogin();
        } else {
            boolean disableLogin = false;
            if (PreferencesEReferral.addBadLogin() >= 3) {
                disableLogin = true;
                PreferencesEReferral.resetBadLogin();
            }
            mCallback.onBadCredentials(disableLogin);
        }
    }

    private Credentials mCredentials;
    private Callback mCallback;

    public void execute(@Nullable Credentials credentials, Callback callback) {
        mCredentials = credentials;
        mCallback = callback;
        run();
    }

    public interface Callback {
        void onCorrectCredentials();

        void onBadCredentials(boolean disableLogin);
    }
}
