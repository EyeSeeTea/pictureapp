package org.eyeseetea.malariacare.phonemetadata;

/**
 * Created by ignac on 12/10/2015.
 */
public class PhoneMetaData {
    private String imei;
    private String phone_number;
    private String phone_serial;

    public PhoneMetaData() {

    }

    public PhoneMetaData(String phone_serial, String imei, String phone_number) {
        this.phone_serial = phone_serial;
        this.imei = imei;
        this.phone_number = phone_number;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_serial() {
        return phone_serial;
    }

    public void setPhone_serial(String phone_serial) {
        this.phone_serial = phone_serial;
    }
}
