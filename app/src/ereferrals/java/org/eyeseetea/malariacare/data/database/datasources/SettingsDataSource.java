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
import org.eyeseetea.sdk.presentation.styles.FontStyle;

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
        boolean isElementActive = isElementActive();
        boolean isMetadataUpdateActive = isMetadataUpdateActive();
        String dhisServerUrl = getDhisServerUrl();
        String wsServerUrl = getWSServerUrl();
        String webUrl = getWebUrl();
        String fontSize = getFontSize();

        return new Settings(systemLanguage, currentLanguage, getMediaListMode(), canDownloadMedia,
                isElementActive, isMetadataUpdateActive, dhisServerUrl, wsServerUrl, webUrl,
                fontSize);
    }

    @Override
    public void saveSettings(Settings settings) {
        setMediaPreference(settings.getMediaListMode().toString());
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

    private boolean isElementActive() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getBoolean(
                context.getResources().getString(R.string.activate_elements_key), false);
    }

    public boolean isMetadataUpdateActive() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getBoolean(context.getString(R.string.check_metadata_key), true);
    }


    private String getFontSize() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        String fontStyleId = sharedPreferences.getString(context.getString(R.string.font_sizes),
                String.valueOf(FontStyle.Medium.getResId()));

        for (FontStyle fontStyle : FontStyle.values()) {
            if (fontStyle.getResId() == Integer.valueOf(fontStyleId)) {
                return fontStyle.getTitle();
            }
        }
        return FontStyle.Medium.getTitle();
    }

    private String getWebUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(
                context.getResources().getString(R.string.web_view_name),
                context.getString(R.string.base_web_view_url));
    }

    private String getWSServerUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(
                context.getResources().getString(R.string.web_service_url),
                context.getString(R.string.ws_base_url));
    }

    private String getDhisServerUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(
                context.getResources().getString(R.string.dhis_url),
                "");
    }

}
