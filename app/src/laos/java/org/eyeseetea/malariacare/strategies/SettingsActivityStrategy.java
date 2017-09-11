package org.eyeseetea.malariacare.strategies;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApi;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.listeners.LoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.layout.listeners.PullRequiredOnPreferenceChangeListener;
import org.eyeseetea.malariacare.network.ServerAPIController;

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
    public void onStart() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public static boolean showAnnouncementOnBackPressed() {
        return  true;
    }
}