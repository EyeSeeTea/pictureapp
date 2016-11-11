package org.eyeseetea.malariacare.VariantAdapter;

import android.content.Intent;

import org.eyeseetea.malariacare.LoginActivity;

public class LoginVariantAdapter extends ALoginVariantAdapter{
    public LoginVariantAdapter(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.startActivity(intent);
    }
}
