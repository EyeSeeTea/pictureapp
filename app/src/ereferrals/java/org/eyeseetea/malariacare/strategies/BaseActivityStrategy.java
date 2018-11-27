package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.GetAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.fragments.ReviewFragment;
import org.eyeseetea.malariacare.fragments.SurveyFragment;
import org.eyeseetea.malariacare.network.ConnectivityStatus;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.LockScreenStatus;
import org.eyeseetea.malariacare.utils.Utils;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    static String TAG = "BaseActivityStrategy";
    private static final int MENU_ITEM_LOGOUT = 99;
    private static final int MENU_ITEM_LOGOUT_ORDER = 106;
    private static final int SETTINGS_LOGOUT = 107;

    LogoutUseCase mLogoutUseCase;

    private int notConnectedText = R.string.offline_status;
    private boolean comesFromNotConected = false;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean notConnected = !ConnectivityStatus.isConnected(
                    PreferencesState.getInstance().getContext());
            android.support.v7.app.ActionBar actionBar = mBaseActivity.getSupportActionBar();
            TextView connection =
                    (TextView) actionBar.getCustomView().findViewById(
                            R.id.action_bar_connection_status);
            connection.setText(translate(notConnected
                    ? R.string.action_bar_offline : R.string.action_bar_online));
            if (notConnected) {
                comesFromNotConected = true;
                Toast.makeText(mBaseActivity,
                        translate(notConnectedText),
                        Toast.LENGTH_SHORT).show();
            } else {
                if(comesFromNotConected){
                    showLoginIfUserReadOnlyMode();
                }
                comesFromNotConected = false;
                Toast.makeText(mBaseActivity,
                        translate(R.string.online_status),
                        Toast.LENGTH_SHORT).show();
            }
            DashboardActivity.dashboardActivity.refreshStatus();
        }
    };

    private void showLoginIfUserReadOnlyMode() {
        IUserRepository userRepository=new UserAccountDataSource();
        GetUserUserAccountUseCase getUserUserAccountUseCase =new GetUserUserAccountUseCase(userRepository);
        getUserUserAccountUseCase.execute(new GetUserUserAccountUseCase.Callback() {
            @Override
            public void onGetUserAccount(UserAccount userAccount) {
                if(!userAccount.canAddSurveys()){
                    showLogin(true);
                }
            }
        });
    }

    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppInBackground()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(false);
        }
    }

    @Override
    public void onStart() {
        mBaseActivity.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(mBaseActivity).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
        applicationWillEnterForeground();
    }

    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(true);
            checkHastSurveyToComplete();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, MENU_ITEM_LOGOUT_ORDER,
                translate(R.string.common_menu_logOff));
        menu.findItem(R.id.action_settings).setTitle(
                translate(R.string.app_settings));
        menu.findItem(R.id.action_about).setTitle(
                translate(R.string.common_menu_about));
        menu.findItem(R.id.action_copyright).setTitle(
                translate(R.string.app_copyright));
        menu.findItem(R.id.action_licenses).setTitle(
                translate(R.string.app_software_licenses));
        menu.findItem(R.id.action_eula).setTitle(
                translate(R.string.app_EULA));
        menu.findItem(R.id.export_db).setTitle(
                translate(R.string.export_data_option_title));
        menu.findItem(R.id.demo_mode).setTitle(
                translate(R.string.run_in_demo_mode));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_LOGOUT:
                new AlertDialog.Builder(mBaseActivity)
                        .setTitle(translate(R.string.common_menu_logOff))
                        .setMessage(translate(
                                R.string.dashboard_menu_logout_message))
                        .setPositiveButton(translate(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        if (mBaseActivity instanceof DashboardActivity) {
                                            ((DashboardActivity) mBaseActivity).executeLogout();
                                        } else {
                                            logout();
                                        }
                                    }
                                })
                        .setNegativeButton(translate(android.R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                break;
            default:
                return false;
        }
        return true;
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
                if (!LockScreenStatus.isPatternSet(mBaseActivity)) {
                    checkIfSurveyIsOpenAndShowLogin();
                }
            }
        }
    };

    private void checkIfSurveyIsOpenAndShowLogin() {
      checkHastSurveyToComplete();
        showLogin(false);
    }
    private void checkHastSurveyToComplete(){
        Fragment f = mBaseActivity.getFragmentManager().findFragmentById(
                R.id.dashboard_details_container);
        if (f instanceof SurveyFragment || f instanceof ReviewFragment) {
            Session.setHasSurveyToComplete(true);
        }else {
            Session.setHasSurveyToComplete(false);
        }
    }

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
        LocalBroadcastManager.getInstance(mBaseActivity).unregisterReceiver(pushReceiver);
        if (EyeSeeTeaApplication.getInstance().isAppInBackground() && !LockScreenStatus.isPatternSet(
                mBaseActivity)) {
            ActivityCompat.finishAffinity(mBaseActivity);
        }
        mBaseActivity.unregisterReceiver(connectionReceiver);
    }

    public void setNotConnectedText(int notConnectedText) {
        this.notConnectedText = notConnectedText;
    }

    @Override
    public void onCreate() {
        mLogoutUseCase = new AuthenticationFactoryStrategy().getLogoutUseCase(mBaseActivity);
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
                showLogin(false);
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
    }

    private void showLogin(boolean pull) {
        Intent loginIntent = new Intent(mBaseActivity, LoginActivity.class);
        loginIntent.putExtra(LoginActivityStrategy.START_PULL, pull);
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

        MenuItem item = menu.findItem(R.id.demo_mode);
        item.setVisible(false);
    }

    @Override
    public void showAbout(final int titleId, int rawId, final Context context) {
        final String stringMessage = mBaseActivity.getMessageWithCommit(rawId, context);
        IAppInfoRepository appInfoDataSource = new AppInfoDataSource(context);
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        GetAppInfoUseCase getAppInfoUseCase = new GetAppInfoUseCase(mainExecutor, asyncExecutor,
                appInfoDataSource);
        getAppInfoUseCase.execute(new GetAppInfoUseCase.Callback() {
            @Override
            public void onAppInfoLoaded(AppInfo appInfo) {
                StringBuilder aboutBuilder = new StringBuilder();
                aboutBuilder.append(
                        String.format(
                                translate(R.string.config_version),
                                appInfo.getConfigFileVersion()));
                aboutBuilder.append("<br/>");
                aboutBuilder.append(
                        String.format(
                                translate(R.string.metadata_update),
                                getUpdateDateFromAppInfo(appInfo)));
                aboutBuilder.append(stringMessage);
                final SpannableString linkedMessage = new SpannableString(
                        Html.fromHtml(aboutBuilder.toString()));
                Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
                mBaseActivity.showAlertWithLogoAndVersion(titleId, linkedMessage, context);
            }
        });


    }

    private String getUpdateDateFromAppInfo(AppInfo appInfo) {
        if (appInfo.getUpdateMetadataDate() == null) {
            return " - ";
        }
        return Utils.parseDateToString(appInfo.getUpdateMetadataDate(),
                "MM/dd/yyyy HH:mm:ss");
    }

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginIfConfigFileObsolete(intent);
        }
    };

    private void showLoginIfConfigFileObsolete(Intent intent) {
        if (intent.getBooleanExtra(PushServiceStrategy.SHOW_LOGIN, false)) {
            SurveyFragment.closeKeyboard();
            showLogin(true);
        }
    }

    public String translate(@StringRes int id){
        return Utils.getInternationalizedString(id, mBaseActivity);
    }

}
