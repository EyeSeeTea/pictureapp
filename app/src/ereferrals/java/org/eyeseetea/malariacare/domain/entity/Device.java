package org.eyeseetea.malariacare.domain.entity;

public class Device {
    private String phone;
    private String IMEI;
    private String androidVersion;

    public Device(String phone, String IMEI, String androidVersion) {
        this.phone = phone;
        this.IMEI = IMEI;
        this.androidVersion = androidVersion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
}
