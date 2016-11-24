package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.network.ServerAPIController;

import java.io.IOException;
import java.util.List;

public class LoginUseCase extends ALoginUseCase{

    public LoginUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute(Credentials credentials) {
        if (credentials.isDemoCredentials()) {
            User user = new User(credentials.getUsername(), credentials.getUsername());

            User.insertLoggedUser(user);

            Session.setUser(user);
            Session.setCredentials(credentials);
        }
        else{
            PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, credentials.getServerURL());
            PreferencesState.getInstance().reloadPreferences();
        }
    }
    @Override
    public boolean isLogoutNeeded(Credentials credentials) {
        return !credentials.getUsername().equals(LoginActivity.DEFAULT_USER) && !credentials.getUsername().equals(
                ServerAPIController.getSDKCredentials().getUsername());
    }
}
