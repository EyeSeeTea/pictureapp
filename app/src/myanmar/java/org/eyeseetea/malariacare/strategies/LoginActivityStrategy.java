package org.eyeseetea.malariacare.strategies;

import android.content.Intent;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

public class LoginActivityStrategy extends ALoginActivityStrategy{
    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
    }

    /**
     * LoginActivity does NOT admin going backwads since it is always the first activity.
     * Thus onBackPressed closes the app
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        if (existsLoggedUser()) {
            loginActivity.finishAndGo(DashboardActivity.class);
        }
    }

    @Override
    public void saveUserCredentials(String serverUrl, String username, String password) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverUrl);
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, username);
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, password);
    }

    private boolean existsLoggedUser(){
        return User.getLoggedUser() != null && !ProgressActivity.PULL_CANCEL;
    }
}
