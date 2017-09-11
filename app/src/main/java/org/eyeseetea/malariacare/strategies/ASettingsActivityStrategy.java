package org.eyeseetea.malariacare.strategies;


import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.SettingsActivity;

public abstract class ASettingsActivityStrategy {

    protected static final String TAG = ".SettingsActivity";

    protected SettingsActivity settingsActivity;

    public ASettingsActivityStrategy(SettingsActivity settingsActivity) {
        this.settingsActivity = settingsActivity;
    }

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void setupPreferencesScreen(PreferenceScreen preferenceScreen);

    public abstract Preference.OnPreferenceClickListener getOnPreferenceClickListener();

    public abstract Preference.OnPreferenceChangeListener getOnPreferenceChangeListener();

    public abstract void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);


    public abstract void onStart();

    public abstract void onBackPressed();

    public abstract void onWindowFocusChanged(boolean hasFocus);

    public void addExtraPreferences() {
    }

    public void onDestroy() {

    }
}
