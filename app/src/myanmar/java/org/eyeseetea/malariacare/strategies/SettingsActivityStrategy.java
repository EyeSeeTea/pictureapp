package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private static final String TAG = ".SettingsStrategy";
    LogoutAndLoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    @Override
    public void onCreate() {
        //Register into sdk bug for listening to logout events
        Dhis2Application.bus.register(this);
    }

    @Override
    public void onStop() {
        try {
            //Unregister from bus before leaving
            Dhis2Application.bus.unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory =
                (PreferenceCategory) preferenceScreen.findPreference(
                        settingsActivity.getResources().getString(R.string.pref_cat_server));
        preferenceCategory.removePreference(preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.org_unit)));
    }

    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return null;
    }

    @Subscribe
    public void onLogoutFinished(UiEvent uiEvent) {
        //No event or not a logout event -> done
        if (uiEvent == null || !uiEvent.getEventType().equals(UiEvent.UiEventType.USER_LOG_OUT)) {
            return;
        }
        Log.i(TAG, "Logging out from sdk...OK");
        LogoutUseCase logoutUseCase = new LogoutUseCase(settingsActivity);
        logoutUseCase.execute();
        Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
        settingsActivity.finish();
        settingsActivity.startActivity(loginIntent);
    }

}

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
class LogoutAndLoginRequiredOnPreferenceClickListener implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    LogoutAndLoginRequiredOnPreferenceClickListener(SettingsActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.settings_menu_logout_title))
                .setMessage(activity.getString(R.string.settings_menu_logout_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //finish activity and go to login
                        Log.i(TAG, "Logging out from sdk...");
                        DhisService.logOutUser(activity);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogPreference) preference).getDialog().dismiss();
                        dialog.cancel();
                    }
                }).create().show();
        Log.i(TAG, "Returning from dialog -> true");
        return true;
    }
}
