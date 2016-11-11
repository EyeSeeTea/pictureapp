package org.eyeseetea.malariacare.VariantAdapter;


import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public abstract class ALoginVariantAdapter {
    protected LoginActivity loginActivity;

    public ALoginVariantAdapter(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    public abstract void onBackPressed();

    public abstract void saveUserCredentials(String serverUrl, String username, String password);
}
