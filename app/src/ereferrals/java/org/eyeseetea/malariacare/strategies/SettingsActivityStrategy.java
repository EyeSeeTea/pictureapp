package org.eyeseetea.malariacare.strategies;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    LogoutAndLoginRequiredOnPreferenceClickListener logoutAndloginRequiredOnPreferenceClickListener;
    LogoutUseCase mLogoutUseCase;
    IAuthenticationManager mAuthenticationManager;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        logoutAndloginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
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

    @Override
    public void onStop() {
        applicationdidenterbackground();
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            ActivityCompat.finishAffinity(settingsActivity);
        }
    }
    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(true);
        }
    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory =
                (PreferenceCategory) preferenceScreen.findPreference(
                        settingsActivity.getResources().getString(R.string.pref_cat_server));
        preferenceCategory.removePreference(preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.org_unit)));
    }

    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return logoutAndloginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return null;
    }

    public static boolean showAnnouncementOnBackPressed() {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onStart() {
        applicationWillEnterForeground();
    }

    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(false);
        }
    }

    @Override
    public void onBackPressed() {
        EyeSeeTeaApplication.getInstance().setIsBackPressed(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        EyeSeeTeaApplication.getInstance().setIsWindowFocused(hasFocus);

        if (EyeSeeTeaApplication.getInstance().isBackPressed() && !hasFocus) {
            EyeSeeTeaApplication.getInstance().setIsBackPressed(false);
            EyeSeeTeaApplication.getInstance().setIsWindowFocused(true);
        }
    }

    @Override
    public void addExtraPreferences() {
        settingsActivity.bindPreferenceSummaryToValue(settingsActivity.findPreference(
                settingsActivity.getString(R.string.web_service_url)));
    }

    @Override
    public void onCreate() {
        mAuthenticationManager = new AuthenticationManager(settingsActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        settingsActivity.registerReceiver(mScreenOffReceiver, screenStateFilter);
    }

    private void showLogin() {
        Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        settingsActivity.startActivity(loginIntent);
    }

    @Override
    public void onDestroy() {
        settingsActivity.unregisterReceiver(mScreenOffReceiver);
    }
}
