package org.eyeseetea.malariacare.domain.usecase;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.util.List;

public class CheckCredentialsWithOrgUnitUseCase implements UseCase {
    public interface Callback {
        void onCorrectCredentials();

        void onBadCredentials();
    }

    private Credentials mCredentials;
    private Callback mCallback;

    public void execute(@Nullable Credentials credentials, Callback callback) {
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
