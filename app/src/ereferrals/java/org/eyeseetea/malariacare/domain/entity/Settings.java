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
    private String wsServerUrl;
    private String webUrl;
    private String fontSize;
    private String user;
    private String pass;
    private String programUrl;

    public Settings(String systemLanguage, String currentLanguage,
            MediaListMode mediaListMode, boolean canDownloadWith3G, boolean isElementActive,
                    boolean isMetadataUpdateActive, String user, String pass, String wsServerUrl,
            String webUrl, String fontSize, String programUrl) {
        this.systemLanguage = required(systemLanguage, "systemLanguage is required");
        this.currentLanguage = currentLanguage;
        this.mediaListMode = mediaListMode;
        this.canDownloadWith3G = canDownloadWith3G;
        this.isElementActive = isElementActive;
        this.isMetadataUpdateActive = isMetadataUpdateActive;
        this.user = required(user, "user is required");
        this.pass = required(pass, "pass is required");
        this.wsServerUrl = wsServerUrl;
        this.webUrl = webUrl;
        this.fontSize = fontSize;
        this.programUrl = programUrl;
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


    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean isCanDownloadWith3G() {
        return canDownloadWith3G;
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

    public String getProgramUrl() {
        return programUrl;
    }
}
