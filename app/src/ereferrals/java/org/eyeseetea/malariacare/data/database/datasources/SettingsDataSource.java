package org.eyeseetea.malariacare.data.database.datasources;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApi;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
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
        String user = loadUser();
        String pass = loadPass();
        String wsServerUrl = getWSServerUrl();
        String webUrl = getWebUrl();
        String fontSize = getFontSize();
        String programUrl = getProgramUrl();
        String programEndPoint = getProgramEndPoint();
        return new Settings(systemLanguage, currentLanguage, getMediaListMode(), canDownloadMedia,
                isElementActive, isMetadataUpdateActive, user, pass, wsServerUrl,
                webUrl, fontSize, programUrl, programEndPoint);
    }

    private String loadPass() {
        String pass = getProgramPassword();
        if (pass == null) {
            try {
                pass = new AuthenticationApi().getHardcodedApiPass();
            } catch (ConfigJsonIOException e) {
                e.printStackTrace();
            }
        }
        return pass;
    }


    private String loadUser() {
        String user = getProgramUser();
        if (user == null) {
            try {
                user = new AuthenticationApi().getHardcodedApiUser();
            } catch (ConfigJsonIOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public void saveSettings(Settings settings) {
        saveMediaPreference(settings.getMediaListMode().toString());
        saveProgramUrl(settings.getProgramUrl());
        saveWebUrl(settings.getWebUrl());
        saveProgramEndPoint(settings.getProgramEndPoint());
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

    private void saveMediaPreference(String listType) {
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

    private void saveProgramUrl(String programUrl) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.program_configuration_url), programUrl);
        editor.commit();
    }

    private void saveProgramEndPoint(String programUrl) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.program_configuration_endpoint), programUrl);
        editor.commit();
    }

    private void saveWebUrl(String webUrl) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.web_view_url), webUrl);
        editor.commit();
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

    private String getProgramUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        //// TODO: 10/09/2018 change the default url from dhis server to new endpoint
        String defaultServer = context.getString(R.string.PROGRAM_DEFAULT_SERVER);
        return sharedPreferences.getString(context.getString(R.string.program_configuration_url), defaultServer);
    }


    private String getProgramEndPoint() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String defaultEndpoint = context.getString(R.string.PROGRAM_DEFAULT_ENDPOINT);
        return sharedPreferences.getString(context.getString(R.string.program_configuration_endpoint), defaultEndpoint);
    }

    private String getProgramUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(context.getString(R.string.program_configuration_user),
                null);
    }

    private String getProgramPassword() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(context.getString(R.string.program_configuration_pass),
                null);
    }
}
