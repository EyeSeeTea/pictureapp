package org.eyeseetea.malariacare.domain.entity;

public class AppInfo {
    private String metadataVersion;
    private String appVersion;

    public AppInfo(String metadataVersion, String appVersion) {
        this.metadataVersion = metadataVersion;
        this.appVersion = appVersion;
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public void setMetadataVersion(String metadataVersion) {
        this.metadataVersion = metadataVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
