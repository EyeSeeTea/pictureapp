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
        if (credentials != null) {
            mCredentials = credentials;
        } else {
            mCredentials = getSavedCredentials();
        }
        mCallback = callback;
        run();
    }

    private Credentials getSavedCredentials() {
        return null;
    }

    @Override
    public void run() {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        for (OrgUnit orgUnit : orgUnits) {
            if (mCredentials.getUsername().equals(orgUnit.getUid())) {
                //TODO check if attribute corresponds with password
                PreferencesEReferral.saveLoggedUserCredentials(mCredentials);
                mCallback.onCorrectCredentials();
                return;
            }
        }
        mCallback.onBadCredentials();
    }
}
