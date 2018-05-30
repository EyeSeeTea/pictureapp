package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository.MediaListMode;

public class Settings {

    private final MediaListMode DEFAULT_LIST_STYLE = MediaListMode.GRID;

    private String systemLanguage;
    private String currentLanguage;
    private MediaListMode mediaListMode;
    private boolean canDownloadWith3G;

    public Settings(String systemLanguage, String currentLanguage,
            MediaListMode mediaListMode, boolean canDownloadWith3G) {
        this.systemLanguage = required(systemLanguage, "systemLanguage is required");
        this.currentLanguage = currentLanguage;
        this.mediaListMode = mediaListMode;
        this.canDownloadWith3G = canDownloadWith3G;
    }

    public String getLanguage() {
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            return systemLanguage;
        } else {
            return currentLanguage;
        }
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
}
