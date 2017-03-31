package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.util.List;

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
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        for (OrgUnit orgUnit : orgUnits) {
            if (mCredentials.getUsername().equals(orgUnit.getUid())) {
                //TODO check if attribute corresponds with password
                mCallback.onCorrectCredentials();
                return;
            }
        }
        mCallback.onBadCredentials();
    }
}
