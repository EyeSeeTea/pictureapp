package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;

public class PreferencesCNM {

    /**
     * Get logged user credentials from sharedPreferences.
     */
    public static boolean isMetadataDownloaded() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getBoolean(context.getString(R.string.metadata_downloaded), false);

    }

    public static void setMetadataDownloaded(boolean downloaded) {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.metadata_downloaded), downloaded);
        editor.commit();
    }

}
