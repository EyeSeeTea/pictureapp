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
    private String url;
    private String user;
    private String pass;

    public Settings(String systemLanguage, String currentLanguage,
            MediaListMode mediaListMode, boolean canDownloadWith3G, boolean isElementActive,
                    boolean isMetadataUpdateActive, String url, String user, String pass) {
        this.systemLanguage = required(systemLanguage, "systemLanguage is required");
        this.currentLanguage = currentLanguage;
        this.mediaListMode = mediaListMode;
        this.canDownloadWith3G = canDownloadWith3G;
        this.isElementActive = isElementActive;
        this.isMetadataUpdateActive = isMetadataUpdateActive;
        this.url = required(url, "url is required");
        this.user = required(user, "user is required");
        this.pass = required(pass, "pass is required");
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

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
