package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class AppInfo {
    private String metadataVersion;
    private String configFileVersion;
    private String appVersion;
    private boolean metadataDownloaded;

    public AppInfo(String metadataVersion, String configFileVersion, String appVersion) {
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
        this.configFileVersion = required(configFileVersion, "configFileVersion is required");
        this.appVersion = required(appVersion, "appVersion is required");
    }

    public AppInfo(boolean metadataDownloaded, String configFileVersion, String metadataVersion) {
        this.metadataDownloaded = required(metadataDownloaded, "metadataDownloaded is required");
        this.configFileVersion = required(configFileVersion, "configFileVersion is required");
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public String getConfigFileVersion() {
        return configFileVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isMetadataDownloaded() {
        return metadataDownloaded;
    }
}
