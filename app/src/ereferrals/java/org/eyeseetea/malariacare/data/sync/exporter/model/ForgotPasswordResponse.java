package org.eyeseetea.malariacare.data.sync.exporter.model;

public class ForgotPasswordResponse {
    public static final String STATUS_ACCEPTED = "Accept";
    public static final String STATUS_DENIED = "Denied";


    private String status;
    private String message;

    public ForgotPasswordResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public java.lang.String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
