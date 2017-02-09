package org.eyeseetea.malariacare.layout.listeners;

import android.app.Activity;
import android.content.Intent;
import android.preference.Preference;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
public class LogoutAndLoginRequiredOnPreferenceClickListener implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    public LogoutAndLoginRequiredOnPreferenceClickListener(SettingsActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        return activity.onPreferenceClick(preference);
    }

    public static void logout(final Activity activity) {
        Log.d(TAG, "Logging out...");
        AuthenticationManager authenticationManager = new AuthenticationManager(activity);
        LogoutUseCase logoutUseCase = new LogoutUseCase(authenticationManager);

        logoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                Intent loginIntent = new Intent(activity, LoginActivity.class);
                activity.finish();
                activity.startActivity(loginIntent);
            }

            @Override
            public void onLogoutError(String message) {
                Log.e(TAG, message);
            }
        });
    }
}
