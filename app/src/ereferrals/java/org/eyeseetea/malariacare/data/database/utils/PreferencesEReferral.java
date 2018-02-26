package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.LoginType;

public class PreferencesEReferral {

    /**
     * Get logged user credentials from sharedPreferences.
     */
    public static Credentials getUserCredentialsFromPreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String url = sharedPreferences.getString(context.getString(R.string.dhis_url), null);

        String username = sharedPreferences.getString(
                context.getString(R.string.logged_user_username), null);
        String password = sharedPreferences.getString(context.getString(R.string.logged_user_pin),
                null);
        if (url == null || url.isEmpty() ||
                username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            return null;
        }

        Credentials credentials = new Credentials(url, username, password);

        return credentials;
    }

    public static void saveLoggedUserCredentials(Credentials credentials) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username = null;
        String pin = null;
        if (credentials != null) {
            username = credentials.getUsername();
            pin = credentials.getPassword();
        }
        editor.putString(context.getString(R.string.logged_user_username), username);
        editor.putString(context.getString(R.string.logged_user_pin), pin);
        editor.commit();
    }

    public static void saveUserProgramId(Long id_program) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getString(R.string.logged_user_program), id_program);
        editor.commit();
    }

    public static long getUserProgramId() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getLong(context.getString(R.string.logged_user_program), -1);
    }

    public static int getNumberBadLogin() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getInt(context.getString(R.string.number_bad_login), 0);
    }

    public static int addBadLogin() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.number_bad_login), getNumberBadLogin() + 1);
        editor.commit();
        return getNumberBadLogin();
    }

    public static int setBadLogin(int numberBadLogged) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.number_bad_login), numberBadLogged);
        editor.commit();
        return getNumberBadLogin();
    }


    public static void resetBadLogin() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.number_bad_login), 0);
        editor.commit();
    }


    public static void setTimeLoginEnables(long timeLoginEnables) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getString(R.string.time_enable_login), timeLoginEnables);
        editor.commit();
    }

    public static long getTimeLoginEnables() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getLong(context.getString(R.string.time_enable_login), 0);
    }

    public static String getWSURL(){
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(context.getString(R.string.web_service_url), context.getString(R.string.ws_base_url));
    }

    public static void setLastLoginType(LoginType loginType){
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.last_log_in_type), loginType.getIntType());
        editor.commit();
    }

    public static LoginType getLastLoginType() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        int intLoginType = sharedPreferences.getInt(context.getString(R.string.last_log_in_type), 0);

        return LoginType.fromInt(intLoginType);
    }

    public static void setWSURL(String url){
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url), url);
        editor.commit();
    }

    public static String getWebViewURL(){
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(context.getString(R.string.web_view_url), context.getString(R.string.base_web_view_url));
    }
    public static void setWebViewURL(String url){
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_view_url), url);
        editor.commit();
    }
}
