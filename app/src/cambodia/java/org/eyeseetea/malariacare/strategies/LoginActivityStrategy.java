package org.eyeseetea.malariacare.strategies;

import android.content.Intent;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public class LoginActivityStrategy extends ALoginActivityStrategy {
    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        loginActivity.onBackPressed();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void finishAndGo() {
        if (loginActivity.getIntent().getBooleanExtra(LoginActivity.PULL_REQUIRED, false)) {
            loginActivity.startActivity(new Intent(loginActivity, ProgressActivity.class));
        } else {
            Intent intent = new Intent(loginActivity, SettingsActivity.class);

            intent.putExtra(SettingsActivity.IS_LOGIN_DONE, true);

            loginActivity.startActivity(intent);
        }

        loginActivity.finish();
    }

}
