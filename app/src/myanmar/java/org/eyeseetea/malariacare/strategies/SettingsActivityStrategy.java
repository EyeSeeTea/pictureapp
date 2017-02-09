package org.eyeseetea.malariacare.strategies;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    LogoutAndLoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStop() {

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

    public boolean onPreferenceClick(final Preference preference) {

        new AlertDialog.Builder(settingsActivity)
                .setTitle(settingsActivity.getString(R.string.app_logout))
                .setMessage(settingsActivity.getString(R.string.settings_menu_logout_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        LogoutAndLoginRequiredOnPreferenceClickListener.logout(settingsActivity);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogPreference) preference).getDialog().dismiss();
                        dialog.cancel();
                    }
                }).create().show();
        return true;
    }
}
