package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public class PreferencesEReferral {

    /**
     * Get logged user credentials from sharedPreferences.
     */
    public static Credentials getUserCredentialsFromPreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String url = sharedPreferences.getString(context.getString(R.string.dhis_url), "");

        String username = sharedPreferences.getString(
                context.getString(R.string.logged_user_username), "");
        String password = sharedPreferences.getString(context.getString(R.string.logged_user_pin),
                "");
        Credentials credentials = new Credentials(url, username, password);

        return credentials;
    }

    public static void saveLoggedUserCredentials(Credentials credentials) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username = credentials.getUsername();
        String pin = credentials.getPassword();
        editor.putString(context.getString(R.string.logged_user_username), username);
        editor.putString(context.getString(R.string.logged_user_pin), pin);
        editor.commit();
    }

}
