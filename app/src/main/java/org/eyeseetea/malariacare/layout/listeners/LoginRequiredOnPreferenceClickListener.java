package org.eyeseetea.malariacare.layout.listeners;

import android.content.Intent;
import android.preference.Preference;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
public class LoginRequiredOnPreferenceClickListener implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity settingsActivity;

    public LoginRequiredOnPreferenceClickListener(SettingsActivity activity) {
        this.settingsActivity = activity;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if (!settingsActivity.getIntent().getBooleanExtra(SettingsActivity.IS_LOGIN_DONE, false)
                || preference.getKey().equals(PreferencesState.getInstance().getContext().getString(R.string.dhis_url))) {
            //if is not logged the pull of data is required.
            PreferencesState.getInstance().setMetaDataDownload(true);
            //only in laos y cambodiates
            PreferencesState.getInstance().setPullDataAfterMetadata(true);

            Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.PULL_REQUIRED, true);


            settingsActivity.startActivity(loginIntent);
        }
        return true;
    }
}
