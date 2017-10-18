package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class AppInfo {
    private String metadataVersion;
    private String appVersion;
    private boolean metadataDownloaded;

    public AppInfo(String metadataVersion, String appVersion) {
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
        this.appVersion = required(appVersion, "appVersion is required");
    }

    public AppInfo(boolean metadataDownloaded) {
        this.metadataDownloaded = required(metadataDownloaded, "metadataDownloaded is required");
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isMetadataDownloaded() {
        return metadataDownloaded;
    }
}
