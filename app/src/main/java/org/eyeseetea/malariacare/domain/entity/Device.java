package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Device {
    private String phone;
    private String IMEI;
    private String androidVersion;

    public Device(String phone, String IMEI, String androidVersion) {
        this.phone = phone;
        this.IMEI = IMEI;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (phone != null ? !phone.equals(device.phone) : device.phone != null) return false;
        if (IMEI != null ? !IMEI.equals(device.IMEI) : device.IMEI != null) return false;
        return androidVersion != null ? androidVersion.equals(device.androidVersion)
                : device.androidVersion == null;
    }

    @Override
    public int hashCode() {
        int result = phone != null ? phone.hashCode() : 0;
        result = 31 * result + (IMEI != null ? IMEI.hashCode() : 0);
        result = 31 * result + (androidVersion != null ? androidVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                "phone='" + phone + '\'' +
                ", IMEI='" + IMEI + '\'' +
                ", androidVersion='" + androidVersion + '\'' +
                '}';
    }
}
