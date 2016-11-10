package org.eyeseetea.malariacare.layout.customization;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
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

                Log.i(TAG, "Creating demo login from login ...");
                LoginUseCase loginUseCase = new LoginUseCase();

                Credentials demoCrededentials = Credentials.createDemoCredentials();

                loginUseCase.execute(demoCrededentials,loginActivity);

/*
                saveDemoServerInPreferences(loginActivity);

                createDummyUser();

                createDummyDataInDB(loginActivity);
*/

                loginActivity.finishAndGo(DashboardActivity.class);
            }
        });
    }

    /*
    private void createDummyDataInDB(LoginActivity loginActivity) {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            Log.i(TAG, "OrgUnits empty, loading dummy data ...");
            try {
                PopulateDB.populateDummyData(loginActivity.getAssets());
            } catch (IOException e) {
                Log.e(TAG, "Has ocurred an error loading dummy data. " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

/   private void createDummyUser() {
        Log.i(TAG, "Creating demo user ...");
        User dummyUser = User.createDummyUser();
        Session.setUser(dummyUser);
    }

    private void saveDemoServerInPreferences(LoginActivity loginActivity) {
        CharSequence demoUrlServer = loginActivity.getResources().getText(R.string.url_server_demo_login);

        Log.i(TAG, "saving  dhis_url preference " + demoUrlServer.toString());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, demoUrlServer.toString());
    }*/
}
