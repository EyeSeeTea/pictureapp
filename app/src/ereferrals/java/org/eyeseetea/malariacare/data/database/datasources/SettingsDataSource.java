package org.eyeseetea.malariacare.data.database.datasources;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;

import java.util.Date;
import java.util.Locale;

public class SettingsDataSource implements ISettingsRepository {
    private final Context context;

    public SettingsDataSource(Context context) {
        this.context = context;
    }

    @Override
    public Settings getSettings() {
        String systemLanguage = getCurrentLocale().getLanguage();
        String currentLanguage = PreferencesState.getInstance().getLanguageCode();
        boolean canDownloadMedia = canDownloadMediaWith3G();
        Date updateMetadataDate = getUpdateMetadataDate();

        return new Settings(systemLanguage, currentLanguage, getMediaListMode(), canDownloadMedia,
                updateMetadataDate);
    }

    @Override
    public void saveSettings(Settings settings) {
        setMediaPreference(settings.getMediaListMode().toString());
        saveUpdateMetadataDate(settings.getUpdateMetadataDate());
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return Resources.getSystem().getConfiguration().locale;
        }
    }

    private MediaListMode getMediaListMode() {
        MediaListMode mediaListMode = null;
        if(getMediaPreference().equals(MediaListMode.GRID.toString())){
            mediaListMode = MediaListMode.GRID;
        }else if(getMediaPreference().equals(MediaListMode.LIST.toString())){
            mediaListMode = MediaListMode.LIST;
        }
        return mediaListMode;
    }

    private void setMediaPreference(String listType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.media_list_style_preference), listType);
        editor.commit();
    }

    private String getMediaPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(
                context.getResources().getString(R.string.media_list_style_preference),
                "");
    }

    private boolean canDownloadMediaWith3G() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getBoolean(
                context.getResources().getString(R.string.allow_media_download_3g_key), false);
    }

    private Date getUpdateMetadataDate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return new Date(sharedPreferences.getLong(
                context.getResources().getString(R.string.update_metadata_date), 0));
    }

    private void saveUpdateMetadataDate(Date updateMetadataName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.update_metadata_date),
                updateMetadataName.getTime());
    }
}
