package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class AppInfo {
    private String metadataVersion;
    private String appVersion;

    public AppInfo(String metadataVersion, String appVersion) {
        this.metadataVersion = required(metadataVersion, "metadataVersion is required");
        this.appVersion = required(appVersion, "appVersion is required");
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }
}
