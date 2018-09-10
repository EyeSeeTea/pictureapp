package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository.MediaListMode;

public class Settings {

    private final MediaListMode DEFAULT_LIST_STYLE = MediaListMode.GRID;

    private String systemLanguage;
    private String currentLanguage;
    private MediaListMode mediaListMode;
    private boolean canDownloadWith3G;
    private boolean isElementActive;
    private boolean isMetadataUpdateActive;
    private String dhisServerUrl;
    private String wsServerUrl;
    private String webUrl;
    private String fontSize;

    public Settings(String systemLanguage, String currentLanguage,
            MediaListMode mediaListMode, boolean canDownloadWith3G, boolean isElementActive,
            boolean isMetadataUpdateActive, String dhisServerUrl, String wsServerUrl,
            String webUrl, String fontSize) {
        this.systemLanguage = required(systemLanguage, "systemLanguage is required");
        this.currentLanguage = currentLanguage;
        this.mediaListMode = mediaListMode;
        this.canDownloadWith3G = canDownloadWith3G;
        this.isElementActive = isElementActive;
        this.isMetadataUpdateActive = isMetadataUpdateActive;
        this.dhisServerUrl = dhisServerUrl;
        this.wsServerUrl = wsServerUrl;
        this.webUrl = webUrl;
        this.fontSize = fontSize;
    }

    public String getLanguage() {
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            return systemLanguage;
        } else {
            return currentLanguage;
        }
    }

    public String getSystemLanguage() {
        return systemLanguage;
    }

    public MediaListMode getMediaListMode() {
        if(mediaListMode==null){
            return DEFAULT_LIST_STYLE;
        }
        return mediaListMode;
    }

    public void setMediaListMode(MediaListMode mediaListMode) {
        this.mediaListMode = mediaListMode;
    }

    public boolean canDownloadWith3G() {
        return canDownloadWith3G;
    }

    public boolean isElementActive() {return isElementActive;}

    public boolean isMetadataUpdateActive() {
        return isMetadataUpdateActive;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean isCanDownloadWith3G() {
        return canDownloadWith3G;
    }

    public String getDhisServerUrl() {
        return dhisServerUrl;
    }

    public String getWsServerUrl() {
        return wsServerUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getFontSize() {
        return fontSize;
    }
}
