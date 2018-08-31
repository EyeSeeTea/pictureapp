package org.eyeseetea.malariacare.strategies;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.fragments.ReviewFragment;
import org.eyeseetea.malariacare.fragments.SurveyFragment;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.LockScreenStatus;

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
    public void onStart() {
        annotateAppInForeground();
    }

    private void annotateAppInForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppInBackground()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(false);
        }
    }

    public void annotateAppInBackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(true);
            checkIfSurveyIsOpenAndSaveSatus();
        }
    }


    @Override
    public void onStop() {
        annotateAppInBackground();
        if (EyeSeeTeaApplication.getInstance().isAppInBackground() && !LockScreenStatus.isPatternSet(
                mBaseActivity)) {
            ActivityCompat.finishAffinity(mBaseActivity);
        }
    }

    @Override
    public void onCreate() {

        mLogoutUseCase = new AuthenticationFactoryStrategy().getLogoutUseCase(mBaseActivity);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();

        mLoginUseCase = new LoginUseCase(mAuthenticationManager, asyncExecutor, mainExecutor);
        mBaseActivity.createActionBar();

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mBaseActivity.registerReceiver(mScreenOffReceiver, screenStateFilter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.removeItem(R.id.demo_mode);
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

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public void runDemoMode() {
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                PreferencesState.getInstance().reloadPreferences();
                updateActionBarTitleAfterLogout();
                loginDemoMode();
            }

            @Override
            public void onLogoutError(String message) {
                Log.e(TAG, message);
            }
        });

    }

    private void updateActionBarTitleAfterLogout() {
        android.support.v7.app.ActionBar actionBar = mBaseActivity.getSupportActionBar();
        LayoutUtils.setActionBarLogo(actionBar);
        LayoutUtils.setActionBarText(actionBar,
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.malaria_case_based_reporting), "");
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
                    public void onServerPinChanged() {

                    }

                    @Override
                    public void onNetworkError() {
                        Log.d(TAG, "onNetworkError");
                    }

                    @Override
                    public void onConfigJsonInvalid() {
                        Log.d(TAG, "onConfigJsonInvalid");
                    }

                    @Override
                    public void onUnexpectedError() {
                        Log.d(TAG, "onUnexpectedError");
                    }

                    @Override
                    public void onMaxLoginAttemptsReachedError() {
                        Log.e(this.getClass().getSimpleName(),
                                "Max Login Attempts Reached Error");
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

    @Override
    public void createActionBar() {
        IProgramRepository programRepository = new ProgramRepository();
        Program userProgram = programRepository.getUserProgram();

        if (userProgram != null && PreferencesState.getInstance().getOrgUnit() != null &&
                !PreferencesState.getInstance().getOrgUnit().isEmpty()) {
            android.support.v7.app.ActionBar actionBar = mBaseActivity.getSupportActionBar();
            LayoutUtils.setActionBarLogo(actionBar);
            LayoutUtils.setActionBarText(actionBar,
                    PreferencesState.getInstance().getOrgUnit() + " - " +
                            userProgram.getCode(),
                    mBaseActivity.getResources().getString(R.string.malaria_case_based_reporting));
        }
    }

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "Screen off");
                if (!LockScreenStatus.isPatternSet(mBaseActivity)) {
                    checkIfSurveyIsOpenAndSaveSatus();
                }
            }
        }
    };

    private void checkIfSurveyIsOpenAndSaveSatus() {
        Fragment f = mBaseActivity.getFragmentManager().findFragmentById(
                R.id.dashboard_details_container);
        if (f instanceof SurveyFragment || f instanceof ReviewFragment) {
            Session.setHasSurveyToComplete(true);
        } else {
            Session.setHasSurveyToComplete(false);
        }
    }

    @Override
    public void onDestroy() {
        mBaseActivity.unregisterReceiver(mScreenOffReceiver);
    }
}