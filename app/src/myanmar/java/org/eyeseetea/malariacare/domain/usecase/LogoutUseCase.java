package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;

public class LogoutUseCase extends ALogoutUseCase {

    public LogoutUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute() {
        User user = User.getLoggedUser();

        if (user != null) {
            user.delete();
        }

        clearCredentials();

        Session.logout();

        PopulateDB.wipeDatabase();
    }

    private void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                context.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }
}
