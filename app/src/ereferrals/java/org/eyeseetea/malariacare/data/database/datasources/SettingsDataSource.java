package org.eyeseetea.malariacare.data.database.datasources;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApi;
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
        String wsVersion = getWSVersion();
        boolean isSoftLoginRequired = getSoftLoginRequired();
        boolean isPullRequired = getPullRequired();
        return new Settings(systemLanguage, currentLanguage, getMediaListMode(), canDownloadMedia,
                isElementActive, isMetadataUpdateActive, user, pass, wsServerUrl,
                webUrl, fontSize, programUrl, programEndPoint, isSoftLoginRequired, isPullRequired,
                wsVersion);
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
        saveSoftLoginRequired(settings.isSoftLoginRequired());
        savePullRequired(settings.isPullRequired());
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
        savePreference(context, R.string.media_list_style_preference, listType);
    }

    private String getMediaPreference() {
        return getPreference(context, R.string.media_list_style_preference, R.string.empty_string);
    }

    private void saveProgramUrl(String programUrl) {
        savePreference(context, R.string.program_configuration_url, programUrl);
    }

    private void saveProgramEndPoint(String programUrl) {
        savePreference(context, R.string.program_configuration_endpoint, programUrl);
    }

    private void saveWebUrl(String webUrl) {
        savePreference(context, R.string.web_view_url, webUrl);
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
        return getBooleanPreference(context, R.string.allow_media_download_3g_key, false);
    }

    private boolean isElementActive() {
        return getBooleanPreference(context, R.string.activate_elements_key, false);
    }

    public boolean isMetadataUpdateActive() {
        return getBooleanPreference(context, R.string.check_metadata_key, true);
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
        return getPreference(context, R.string.web_view_name, R.string.base_web_view_url);
    }

    private String getWSServerUrl() {
        return getPreference(context, R.string.web_service_url, R.string.ws_base_url);
    }

    private String getProgramUrl() {
        return getPreference(context, R.string.program_configuration_url, R.string.PROGRAM_DEFAULT_SERVER);
    }


    private String getProgramEndPoint() {
        return getPreference(context, R.string.program_configuration_endpoint, R.string.PROGRAM_DEFAULT_ENDPOINT);
    }

    private String getProgramUser() {
        return getPreference(context, R.string.program_configuration_user, null);
    }

    private String getProgramPassword() {
        return getPreference(context, R.string.program_configuration_pass, null);
    }

    private String getWSVersion() {
        return context.getString(R.string.ws_version);
    }

    private boolean getSoftLoginRequired() {
        return getBooleanPreference(context, R.string.soft_login_required, false);
    }

    private void saveSoftLoginRequired(boolean isRequired) {
        saveBooleanPreference(context, R.string.soft_login_required, isRequired);
    }

    private boolean getPullRequired() {
        return getBooleanPreference(context, R.string.pull_required, false);
    }

    private void savePullRequired(boolean isRequired) {
        saveBooleanPreference(context, R.string.pull_required, isRequired);
    }

    private Boolean getBooleanPreference(Context context, int stringId, Boolean defaultBool) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getBoolean(
                context.getResources().getString(stringId),
                defaultBool);
    }

    private void saveBooleanPreference(Context context, int stringId, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(stringId), value);
        editor.commit();
    }

    private String getPreference(Context context, int stringId, Integer defaultStringId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String value = null;
        if(defaultStringId!=null){
            value = context.getString(defaultStringId);
        }
        return sharedPreferences.getString(
                context.getResources().getString(stringId),
                value);
    }

    private void savePreference(Context context, int stringId, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(stringId), value);
        editor.commit();
    }
}
