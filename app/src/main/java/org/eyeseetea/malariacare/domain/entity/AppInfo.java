package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.Date;

public class AppInfo {
    private String metadataVersion;
    private String configFileVersion;
    private String appVersion;
    private boolean metadataDownloaded;
    private Date updateMetadataDate;

    public AppInfo(String metadataVersion, String configFileVersion, String appVersion,
            Date updateMetadataDate) {
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
        this.configFileVersion = required(configFileVersion, "configFileVersion is required");
        this.appVersion = required(appVersion, "appVersion is required");
        this.updateMetadataDate = updateMetadataDate;
    }

    public AppInfo(boolean metadataDownloaded, String configFileVersion, String metadataVersion) {
        this.metadataDownloaded = required(metadataDownloaded, "metadataDownloaded is required");
        this.configFileVersion = required(configFileVersion, "configFileVersion is required");
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public void changeMetadataDownloaded(boolean metadataDownloaded) {
        this.metadataDownloaded = metadataDownloaded;
    }

    public String getConfigFileVersion() {
        return configFileVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void changeMetadataVersion(String metadataVersion) {
        this.metadataVersion = metadataVersion;
    }

    public boolean isMetadataDownloaded() {
        return metadataDownloaded;
    }

    public Date getUpdateMetadataDate() {
        return updateMetadataDate;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "metadataVersion='" + metadataVersion + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", metadataDownloaded=" + metadataDownloaded +
                '}';
    }
}
