package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
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
        if (!activity.getIntent().getBooleanExtra(SettingsActivity.IS_LOGIN_DONE, false)) {

            String orgUnitValue = activity.autoCompleteEditTextPreference.getText();

            Intent loginIntent = new Intent(activity, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.PULL_REQUIRED, orgUnitValue.isEmpty());

            activity.startActivity(loginIntent);
        }
        return true;
    }

    protected void logout() {
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