package org.eyeseetea.malariacare.domain.entity;

public class Settings {
    private static final String DEFAULT_LANGUAGE = "en";

    private String language;

    public Settings(String language) {
        this.language = language;
    }

    public String getLanguage() {
        if (language == null || language.isEmpty()) language = DEFAULT_LANGUAGE;
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
