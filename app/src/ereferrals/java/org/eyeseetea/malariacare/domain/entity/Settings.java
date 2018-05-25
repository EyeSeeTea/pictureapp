package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository.MediaListMode;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Settings {

    private final MediaListMode DEFAULT_LIST_STYLE = MediaListMode.GRID;

    private String systemLanguage;
    private String currentLanguage;
    private MediaListMode mediaListMode;

    public Settings(String systemLanguage, String currentLanguage, MediaListMode mediaListMode) {
        this.systemLanguage = required(systemLanguage, "systemLanguage is required");
        this.currentLanguage = currentLanguage;
        this.mediaListMode = mediaListMode;
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
}
