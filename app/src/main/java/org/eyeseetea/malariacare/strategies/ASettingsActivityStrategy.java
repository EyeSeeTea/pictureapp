package org.eyeseetea.malariacare.strategies;


import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.SettingsActivity;

public abstract class ASettingsActivityStrategy {

    protected SettingsActivity settingsActivity;

    public ASettingsActivityStrategy(SettingsActivity settingsActivity) {
        this.settingsActivity = settingsActivity;
    }

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void setupPreferencesScreen(PreferenceScreen preferenceScreen);

    public abstract Preference.OnPreferenceClickListener getOnPreferenceClickListener();

    public abstract Preference.OnPreferenceChangeListener getOnPreferenceChangeListener();
}
