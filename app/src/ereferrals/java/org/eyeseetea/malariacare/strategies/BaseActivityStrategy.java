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
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    static String TAG = "BaseActivityStrategy";
    private static final int MENU_ITEM_LOGOUT = 99;
    private static final int MENU_ITEM_LOGOUT_ORDER = 106;
    private static final int SETTINGS_LOGOUT = 107;

    LogoutUseCase mLogoutUseCase;
    private IAuthenticationManager mAuthenticationManager;
    private int notConnectedText = R.string.offline_status;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onCreate() {
        mAuthenticationManager = new AuthenticationManager(mBaseActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
        mBaseActivity.registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(false);
        }
    }

    @Override
    public void onStart() {
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

    public void logout() {
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                ActivityCompat.finishAffinity(mBaseActivity);
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
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
        applicationdidenterbackground();
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            logout();
        }
        mBaseActivity.unregisterReceiver(connectionReceiver);
    }

    public void setNotConnectedText(int notConnectedText) {
        this.notConnectedText = notConnectedText;
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
}
