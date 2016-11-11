package org.eyeseetea.malariacare.variantadapter;

import android.content.Intent;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

public class LoginVariantAdapter extends ALoginVariantAdapter{
    public LoginVariantAdapter(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        loginActivity.onBackPressed();
    }

    @Override
    public void saveUserCredentials(String serverUrl, String username, String password) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverUrl);
    }
}
