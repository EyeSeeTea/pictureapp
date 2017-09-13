package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Device {
    private String phone;
    private String IMEI;
    private String androidVersion;

    public Device(String phone, String IMEI, String androidVersion) {
        this.phone = required(phone, "phone is required");
        this.IMEI = required(IMEI, "IMEI is required");
        this.androidVersion = required(androidVersion, "androidVersion is required");
    }

    public String getPhone() {
        return phone;
    }

    public String getIMEI() {
        return IMEI;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }
}
