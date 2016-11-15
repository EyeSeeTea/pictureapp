package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.hisp.dhis.android.sdk.ui.views.FontButton;

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
        else{
            addDemoButton();
        }
    }

    private boolean existsLoggedUser(){
        return User.getLoggedUser() != null && !ProgressActivity.PULL_CANCEL;
    }

    private void addDemoButton() {
        ViewGroup loginViewsContainer =(ViewGroup) loginActivity.findViewById(R.id.login_views_container);

        loginActivity.getLayoutInflater().inflate(R.layout.demo_login_button, loginViewsContainer, true);

        FontButton demoButton = (FontButton) loginActivity.findViewById(R.id.demo_login_button);

        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Credentials demoCrededentials = Credentials.createDemoCredentials();

                loginActivity.mLoginUseCase.execute(demoCrededentials);

                loginActivity.finishAndGo(DashboardActivity.class);
            }
        });
    }
}
