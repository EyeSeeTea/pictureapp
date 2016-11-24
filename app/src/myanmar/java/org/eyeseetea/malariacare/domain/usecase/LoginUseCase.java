package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.io.IOException;
import java.util.List;

public class LoginUseCase extends ALoginUseCase {

    public LoginUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute(Credentials credentials) {
        User user = new User(credentials.getUsername(), credentials.getUsername());

        User.insertLoggedUser(user);

        Session.setUser(user);
        Session.setCredentials(credentials);

        saveCredentials(credentials);

        if (credentials.isDemoCredentials()) {
            createDummyDataInDB(context);
        }
    }

    private void saveCredentials(Credentials credentials) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                credentials.getServerURL());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user,
                credentials.getUsername());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password,
                credentials.getPassword());
        PreferencesState.getInstance().reloadPreferences();
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

    @Override
    public boolean isLogoutNeeded(Credentials credentials) {
        return false;
    }
}
