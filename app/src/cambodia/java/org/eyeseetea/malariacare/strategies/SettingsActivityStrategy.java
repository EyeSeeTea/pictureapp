package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.preference.Preference;

import org.eyeseetea.malariacare.LoginActivity;
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
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

}
