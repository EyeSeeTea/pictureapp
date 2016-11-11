package org.eyeseetea.malariacare.VariantAdapter;

import android.content.Intent;

import org.eyeseetea.malariacare.LoginActivity;

public class LoginVariantAdapter extends ALoginVariantAdapter{
    public LoginVariantAdapter(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        loginActivity.onBackPressed();
    }
}
