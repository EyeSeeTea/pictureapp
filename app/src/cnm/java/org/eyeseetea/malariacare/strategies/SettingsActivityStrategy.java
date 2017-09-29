package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.SplashScreenActivity;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.listeners.LoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.layout.listeners.PullRequiredOnPreferenceChangeListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private PullRequiredOnPreferenceChangeListener pullRequiredOnPreferenceChangeListener;


    private static final String TAG = ".SettingsStrategy";
    LoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener =
                new LoginRequiredOnPreferenceClickListener(
                        settingsActivity);

        pullRequiredOnPreferenceChangeListener = new PullRequiredOnPreferenceChangeListener();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStop() {

    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {
        if (!PreferencesState.getInstance().isDevelopOptionActive()) {
            PreferenceCategory preferenceCategory =
                    (PreferenceCategory) preferenceScreen.findPreference(
                            settingsActivity.getResources().getString(R.string.pref_cat_server));
            preferenceCategory.removePreference(preferenceScreen.findPreference(
                    settingsActivity.getResources().getString(R.string.dhis_url)));
        }
        Preference autoconfigurePreference = preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.autoconfigure_preference));
        autoconfigurePreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        launchAutoconfigure();
                        return true;
                    }
                });
    }


    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return pullRequiredOnPreferenceChangeListener;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PreferencesState.getInstance().getContext().getString(R.string.developer_option))){
            settingsActivity.restartActivity();
        }
    }

    public static boolean showAnnouncementOnBackPressed() {
        return true;
    }
    @Override
    public void onStart() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    private void launchAutoconfigure() {
        Intent intent = new Intent(settingsActivity, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        settingsActivity.startActivity(intent);
        settingsActivity.finish();
    }

}
