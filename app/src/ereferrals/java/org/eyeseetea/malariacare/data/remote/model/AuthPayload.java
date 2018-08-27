package org.eyeseetea.malariacare.data.remote.model;

public class AuthPayload {
    private String userCode;
    private String pin;

    public AuthPayload(String userCode, String pin) {
        this.userCode = userCode;
        this.pin = pin;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
