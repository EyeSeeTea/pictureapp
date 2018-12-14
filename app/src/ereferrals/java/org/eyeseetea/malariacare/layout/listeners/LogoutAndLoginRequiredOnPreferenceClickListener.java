package org.eyeseetea.malariacare.layout.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
public class LogoutAndLoginRequiredOnPreferenceClickListener implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity settingsActivity;

    public LogoutAndLoginRequiredOnPreferenceClickListener(SettingsActivity activity) {
        this.settingsActivity = activity;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        new AlertDialog.Builder(settingsActivity)
                .setTitle(settingsActivity.getString(R.string.common_menu_logOff))
                .setMessage(settingsActivity.getString(R.string.settings_menu_logout_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        logout();
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

    private void logout() {
        Log.d(TAG, "Logging out...");

        LogoutUseCase logoutUseCase =
                new AuthenticationFactoryStrategy().getLogoutUseCase(settingsActivity);
        AlarmPushReceiver.cancelPushAlarm(settingsActivity);
        logoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
                settingsActivity.finish();
                settingsActivity.startActivity(loginIntent);
            }

            @Override
            public void onLogoutError(String message) {
                Log.e(TAG, message);
            }
        });
    }
}
