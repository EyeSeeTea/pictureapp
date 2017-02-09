package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.layout.listeners.PullRequiredOnPreferenceChangeListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private PullRequiredOnPreferenceChangeListener pullRequiredOnPreferenceChangeListener;


    private static final String TAG = ".SettingsStrategy";
    LogoutAndLoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(
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


    public boolean onPreferenceClick(final Preference preference){
        if (!settingsActivity.getIntent().getBooleanExtra(SettingsActivity.IS_LOGIN_DONE, false)) {

            String orgUnitValue = settingsActivity.autoCompleteEditTextPreference.getText();

            Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.PULL_REQUIRED, orgUnitValue.isEmpty());

            settingsActivity.startActivity(loginIntent);
        }
        return true;
    }
}

