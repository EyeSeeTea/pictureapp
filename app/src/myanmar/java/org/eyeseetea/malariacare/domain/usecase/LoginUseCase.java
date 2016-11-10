package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.io.IOException;
import java.util.List;

public class LoginUseCase extends ALoginUseCase{
    @Override
    protected void executeCustomActions(Credentials credentials, Context context) {
       if (credentials.isDemoCredentials()){
           createDummyDataInDB(context);
       }
    }

    private void createDummyDataInDB(Context context) {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(context.getAssets());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDemoServerInPreferences(String serverURL, Context context) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverURL);
    }
}
