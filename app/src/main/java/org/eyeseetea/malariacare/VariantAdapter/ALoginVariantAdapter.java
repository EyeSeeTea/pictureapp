package org.eyeseetea.malariacare.variantadapter;


import org.eyeseetea.malariacare.LoginActivity;

public abstract class ALoginVariantAdapter {
    protected LoginActivity loginActivity;

    public ALoginVariantAdapter(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    public abstract void onBackPressed();

    public abstract void saveUserCredentials(String serverUrl, String username, String password);
}
