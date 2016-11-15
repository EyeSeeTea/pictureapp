package org.eyeseetea.malariacare.layout.customization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.views.FontButton;

import java.io.IOException;
import java.util.List;

public class LoginActivityCustomization {
    private String TAG = ".loginCustomization";

    public void onCreate(LoginActivity loginActivity){
        addDemoButton(loginActivity);
    }

    private void addDemoButton(final LoginActivity loginActivity) {
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
