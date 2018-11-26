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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.CustomFontStyles;
import org.eyeseetea.malariacare.utils.LockScreenStatus;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.styles.FontStyle;

import java.util.List;

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
                showLogin();
            }
        }
    };

    @Override
    public void onStop() {
        applicationdidenterbackground();
        LocalBroadcastManager.getInstance(settingsActivity).unregisterReceiver(pushReceiver);
        if (EyeSeeTeaApplication.getInstance().isAppInBackground() && !LockScreenStatus.isPatternSet(
                settingsActivity)) {
            ActivityCompat.finishAffinity(settingsActivity);
        }
    }
    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(true);
        }
    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory =
                (PreferenceCategory) preferenceScreen.findPreference(
                        settingsActivity.getResources().getString(R.string.pref_cat_server));
        preferenceCategory.removePreference(preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.org_unit)));
        if (!PreferencesState.getInstance().isDevelopOptionActive()
                || !BuildConfig.developerOptions) {
            preferenceCategory.removePreference(preferenceScreen.findPreference(
                    settingsActivity.getResources().getString(R.string.drive_key)));
        }
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
        if (key.equals(
                PreferencesState.getInstance().getContext().getString(R.string.developer_option))) {
            settingsActivity.restartActivity();
        }
    }

    @Override
    public void onStart() {
        applicationWillEnterForeground();
        LocalBroadcastManager.getInstance(settingsActivity).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
    }
    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppInBackground()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(false);
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
        settingsActivity.bindPreferenceSummaryToValue(
                settingsActivity.findPreference(settingsActivity.getString(R.string.web_view_url)));
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
        if (!LockScreenStatus.isPatternSet(settingsActivity)) {
            Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            settingsActivity.startActivity(loginIntent);
        }
    }

    @Override
    public void onDestroy() {
        settingsActivity.unregisterReceiver(mScreenOffReceiver);
    }

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginIfConfigFileObsolete(intent);
        }

        private void showLoginIfConfigFileObsolete(Intent intent) {
            if (intent.getBooleanExtra(PushServiceStrategy.SHOW_LOGIN, false)) {
                showLogin();
            }
        }
    };

    @Override
    public void addFontStyleEntries(List<String> entries, List<String> entryValues) {
        for (CustomFontStyles fontStyle:CustomFontStyles.values()) {
            entries.add(Utils.getInternationalizedString(fontStyle.getTitle()));
            entryValues.add(String.valueOf(fontStyle.getResId()));
        }
    }
}
