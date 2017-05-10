package org.eyeseetea.malariacare.strategies;


import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    LogoutAndLoginRequiredOnPreferenceClickListener logoutAndloginRequiredOnPreferenceClickListener;
    LogoutUseCase mLogoutUseCase;
    IAuthenticationManager mAuthenticationManager;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        logoutAndloginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    @Override
    public void onCreate() {
        mAuthenticationManager = new AuthenticationManager(settingsActivity);
        mLogoutUseCase = new LogoutUseCase(mAuthenticationManager);
    }

    @Override
    public void onStop() {
        applicationdidenterbackground();
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            logout();
        }
    }
    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(true);
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
        return logoutAndloginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return null;
    }

    public static boolean showAnnouncementOnBackPressed() {
        return false;
    }

    @Override
    public void onStart() {
        applicationWillEnterForeground();
    }

    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppWentToBg()) {
            EyeSeeTeaApplication.getInstance().setIsAppWentToBg(false);
        }
    }

    @Override
    public void onBackPressed() {
        EyeSeeTeaApplication.getInstance().setIsBackPressed(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        EyeSeeTeaApplication.getInstance().setIsWindowFocused(hasFocus);

        if (EyeSeeTeaApplication.getInstance().isBackPressed() && !hasFocus) {
            EyeSeeTeaApplication.getInstance().setIsBackPressed(false);
            EyeSeeTeaApplication.getInstance().setIsWindowFocused(true);
        }
    }

    private void logout() {
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                ActivityCompat.finishAffinity(settingsActivity);
            }

            @Override
            public void onLogoutError(String message) {
                Log.d(TAG, message);
            }
        });
    }

}
