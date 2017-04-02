package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.view.MenuItem;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.domain.usecase.LoadUserAndCredentialsUseCase;

public class LoginActivityStrategy extends ALoginActivityStrategy {
    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(loginActivity, SettingsActivity.class);

        intent.putExtra(SettingsActivity.IS_LOGIN_DONE, false);

        loginActivity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        LoadUserAndCredentialsUseCase loadUserAndCredentialsUseCase =
                new LoadUserAndCredentialsUseCase(loginActivity);

        loadUserAndCredentialsUseCase.execute();
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


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}
