package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.layout.listeners.LoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.layout.listeners.PullRequiredOnPreferenceChangeListener;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private static final String TAG = ".SettingsActivityStrategy";

    private PullRequiredOnPreferenceChangeListener pullRequiredOnPreferenceChangeListener;
    private LoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener = new LoginRequiredOnPreferenceClickListener(
                settingsActivity);

        pullRequiredOnPreferenceChangeListener = new PullRequiredOnPreferenceChangeListener();
    }

    @Override
    public void onCreate() {
        Dhis2Application.bus.register(this);
    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {

    }

    @Override
    public void onStop() {
        try {
            //Unregister from bus before leaving
            Dhis2Application.bus.unregister(this);
        } catch (Exception e) {
        }
    }

    @Subscribe
    public void callbackLoginPrePull(NetworkJob.NetworkJobResult<ResourceType> result) {
        if (PushController.getInstance().isPushInProgress()) {
            return;
        }
        //Nothing to check
        if (result == null || result.getResourceType() == null || !result.getResourceType().equals(
                ResourceType.USERS)) {
            return;
        }

        //Login failed
        if (result.getResponseHolder().getApiException() != null) {
            new AlertDialog.Builder(settingsActivity)
                    .setTitle(R.string.dhis_url_error)
                    .setMessage(R.string.dhis_url_error_bad_credentials)
                    .setNeutralButton(android.R.string.yes, null)
                    .create()
                    .show();
        }

        //Login successful start reload
        settingsActivity.finish();
        settingsActivity.startActivity(new Intent(settingsActivity, ProgressActivity.class));
    }

    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return pullRequiredOnPreferenceChangeListener;
    }
}