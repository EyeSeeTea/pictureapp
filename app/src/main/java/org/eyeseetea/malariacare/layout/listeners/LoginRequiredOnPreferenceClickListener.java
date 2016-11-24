package org.eyeseetea.malariacare.layout.listeners;


import android.content.Intent;
import android.preference.Preference;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.SettingsActivity;

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
public class LoginRequiredOnPreferenceClickListener implements
        Preference.OnPreferenceClickListener {
    private static final String TAG = "LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    public LoginRequiredOnPreferenceClickListener(SettingsActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (!activity.getIntent().getBooleanExtra(SettingsActivity.IS_LOGIN_DONE, false)) {

            String orgUnitValue = activity.autoCompleteEditTextPreference.getText();

            Intent loginIntent = new Intent(activity, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.PULL_REQUIRED, orgUnitValue.isEmpty());

            activity.startActivity(loginIntent);
        }
        return true;
    }
}
