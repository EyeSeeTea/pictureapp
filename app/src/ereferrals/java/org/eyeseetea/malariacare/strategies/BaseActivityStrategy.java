package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    static String TAG = "BaseActivityStrategy";
    private static final int MENU_ITEM_LOGOUT = 99;
    private static final int MENU_ITEM_LOGOUT_ORDER = 106;
    private static final int SETTINGS_LOGOUT = 107;

    LogoutUseCase mLogoutUseCase;
    private IAuthenticationManager mAuthenticationManager;
    private int notConnectedText = R.string.offline_status;
    LoginUseCase mLoginUseCase;
    private Menu mMenu;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
        mAuthenticationManager = new AuthenticationManager(mBaseActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ICredentialsRepository credentialsLocalDataSoruce = new CredentialsLocalDataSource();
        IOrganisationUnitRepository organisationDataSource = new OrganisationUnitRepository();
        IInvalidLoginAttemptsRepository
                iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        mLoginUseCase = new LoginUseCase(mAuthenticationManager, mainExecutor,
                asyncExecutor, organisationDataSource, credentialsLocalDataSoruce,
                iInvalidLoginAttemptsRepository);
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ConnectivityStatus.isConnected(mBaseActivity)) {
                Toast.makeText(mBaseActivity, notConnectedText, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mBaseActivity, R.string.online_status, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(false);
        }
    }

    @Override
    public void onStart() {
        mBaseActivity.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        applicationWillEnterForeground();
    }

    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(true);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, MENU_ITEM_LOGOUT_ORDER,
                mBaseActivity.getResources().getString(R.string.app_logout));
        mMenu = menu;
        MenuItem item = mMenu.findItem(R.id.demo_mode);
        item.setVisible(PreferencesState.getInstance().isDevelopOptionActive());
        changeDemoModeText();
    }

    private void changeDemoModeText() {
        MenuItem demoModeMenuItem = mMenu.findItem(R.id.demo_mode);
        if (isDemoModeActivated()) {
            demoModeMenuItem.setTitle(R.string.clean_demo_db);
        } else {
            demoModeMenuItem.setTitle(R.string.run_in_demo_mode);
        }
    }

    private boolean isDemoModeActivated() {
        return Session.getCredentials().isDemoCredentials();
    }

    public void runDemoMode() {
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                PreferencesState.getInstance().reloadPreferences();
                loginDemoMode();
            }

            @Override
            public void onLogoutError(String message) {
                Log.e(TAG, message);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_LOGOUT:
                new AlertDialog.Builder(mBaseActivity)
                        .setTitle(mBaseActivity.getString(R.string.app_logout))
                        .setMessage(mBaseActivity.getString(R.string.dashboard_menu_logout_message))
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        if (mBaseActivity instanceof DashboardActivity) {
                                            ((DashboardActivity) mBaseActivity).executeLogout();
                                        } else {
                                            logout();
                                        }
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                break;
            case R.id.demo_mode:
                runDemoMode();
                break;
            default:
                return false;
        }
        return true;
    }

    private void updateActionBarTitleAfterLogout() {
        android.support.v7.app.ActionBar actionBar = mBaseActivity.getSupportActionBar();

        LayoutUtils.setActionBarAppAndUser(actionBar);
    }

    private void loginDemoMode() {
        mLoginUseCase.execute(Credentials.createDemoCredentials(),
                new LoginUseCase.Callback() {
                    @Override
                    public void onLoginSuccess() {
                        changeDemoModeText();
                        updateActionBarTitleAfterLogout();
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

    @Override
    public void onBackPressed() {
        if (!(mBaseActivity instanceof DashboardActivity)) {
            EyeSeeTeaApplication.getInstance().setIsBackPressed(true);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        EyeSeeTeaApplication.getInstance().setIsWindowFocused(hasFocus);

        if (EyeSeeTeaApplication.getInstance().isBackPressed() && !hasFocus) {
            EyeSeeTeaApplication.getInstance().setIsBackPressed(false);
            EyeSeeTeaApplication.getInstance().setIsWindowFocused(true);
        }
    }

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "Screen off");
                //// FIXME: 30/05/2017 Uncomment this line to reactivate the disable login feature
                //showLogin();
            }
        }
    };

    public void showCopyRight(int app_copyright, int copyright) {
        mBaseActivity.showAlertWithMessage(app_copyright, copyright);
    }


    @Override
    public void goSettings() {
        Intent intentSettings = new Intent(mBaseActivity, SettingsActivity.class);
        intentSettings.putExtra(SettingsActivity.SETTINGS_CALLER_ACTIVITY, this.getClass());
        intentSettings.putExtra(SettingsActivity.IS_LOGIN_DONE, false);
        mBaseActivity.startActivityForResult(new Intent(mBaseActivity, SettingsActivity.class),
                SETTINGS_LOGOUT);
    }

    @Override
    public void onStop() {
        applicationdidenterbackground();
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            ActivityCompat.finishAffinity(mBaseActivity);
        }
        mBaseActivity.unregisterReceiver(connectionReceiver);
    }

    public void setNotConnectedText(int notConnectedText) {
        this.notConnectedText = notConnectedText;
    }

    @Override
    public void onCreate() {
        mAuthenticationManager = new AuthenticationManager(mBaseActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mBaseActivity.registerReceiver(mScreenOffReceiver, screenStateFilter);
    }

    public void logout() {
        AlarmPushReceiver.cancelPushAlarm(mBaseActivity);
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                showLogin();
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
    }

    private void showLogin() {
        Intent loginIntent = new Intent(mBaseActivity, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mBaseActivity.startActivity(loginIntent);
    }

    @Override
    public void onDestroy() {
        mBaseActivity.unregisterReceiver(mScreenOffReceiver);
    }

    @Override
    public void hideMenuItems(Menu menu) {
        super.hideMenuItems(menu);
    }


    public static void reloadDashboard() {
        Intent surveysIntent = new Intent(PreferencesState.getInstance().getContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().startService(surveysIntent);
    }
}
