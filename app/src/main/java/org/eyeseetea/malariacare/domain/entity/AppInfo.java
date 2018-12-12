package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.Date;

public class AppInfo {
    private static final long MIN_TIME_CAN_PUSH = 30000;

    private String metadataVersion;
    private String configFileVersion;
    private String appVersion;
    private boolean metadataDownloaded;
    private Date updateMetadataDate;
    private Date lastPushDate;

    public AppInfo(String metadataVersion, String configFileVersion, String appVersion,
            Date updateMetadataDate, Date lastPushDate) {
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
        this.configFileVersion = required(configFileVersion, "configFileVersion is required");
        this.appVersion = required(appVersion, "appVersion is required");
        this.updateMetadataDate = updateMetadataDate;
        this.lastPushDate = lastPushDate;
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

    public Date getLastPushDate() {
        return lastPushDate;
    }

    public boolean canMakeManualPush() {
        Date now = new Date();
        return now.getTime() - getLastPushDate().getTime() >= MIN_TIME_CAN_PUSH;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "metadataVersion='" + metadataVersion + '\'' +
                ", configFileVersion='" + configFileVersion + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", metadataDownloaded=" + metadataDownloaded +
                ", updateMetadataDate=" + updateMetadataDate +
                ", lastPushDate=" + lastPushDate +
                '}';
    }
}
