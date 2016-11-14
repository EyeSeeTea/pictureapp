package org.eyeseetea.malariacare.strategies;

import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.layout.listeners.LoginRequiredOnPreferenceClickListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private static final String TAG=".SettingsActivityStrategy";

    LoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener = new LoginRequiredOnPreferenceClickListener(settingsActivity);
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
}