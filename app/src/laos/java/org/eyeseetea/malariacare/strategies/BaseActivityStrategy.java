package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.services.SurveyService;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    LogoutUseCase mLogoutUseCase;
    IAuthenticationManager mAuthenticationManager;
    LoginUseCase mLoginUseCase;
    BaseActivity mBaseActivity;
    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
        mBaseActivity = baseActivity;
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onCreate() {

        mAuthenticationManager = new AuthenticationManager(mBaseActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
        mLoginUseCase = new LoginUseCase(mAuthenticationManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.demo_mode:
                runDemoMode();
                break;
            default:
                return false;
        }
        return true;
    }

    public void runDemoMode() {
        PopulateDB.wipeSurveys();
        PreferencesState.getInstance().saveStringPreference(R.string.org_unit, "");
        PreferencesState.getInstance().reloadPreferences();
        android.support.v7.app.ActionBar actionBar = mBaseActivity.getSupportActionBar();
        LayoutUtils.setActionBarLogo(actionBar);
        LayoutUtils.setActionBarText(actionBar,
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.malaria_case_based_reporting), "");
        loginDemoMode();
    }

    private void loginDemoMode() {
        mLoginUseCase.execute(Credentials.createDemoCredentials(),
                new LoginUseCase.Callback() {
                    @Override
                    public void onLoginSuccess() {
                        reloadDashboard();
                        Log.d(TAG, "login successful");
                    }

                    @Override
                    public void onServerURLNotValid() {
                        Log.d(TAG, "onServerURLNotValid");
                    }

                    @Override
                    public void onInvalidCredentials() {
                        Log.d(TAG, "onInvalidCredentials");
                    }

                    @Override
                    public void onNetworkError() {
                        Log.d(TAG, "onNetworkError");
                    }

                    @Override
                    public void onConfigJsonNotPresent() {
                        Log.d(TAG, "onConfigJsonNotPresent");
                    }
                }
        );
    }

    public static void reloadDashboard() {
        Intent surveysIntent = new Intent(PreferencesState.getInstance().getContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().startService(surveysIntent);
    }

    public void showCopyRight(int app_copyright, int copyright) {
        mBaseActivity.showAlertWithHtmlMessage(app_copyright, copyright);
    }
}