package org.eyeseetea.malariacare.data.sync.exporter.model;

public class ForgotPasswordPayload {
    private String version;
    private String username;
    private String language;

    public ForgotPasswordPayload(String version, String username, String language) {
        this.version = version;
        this.username = username;
        this.language = language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
