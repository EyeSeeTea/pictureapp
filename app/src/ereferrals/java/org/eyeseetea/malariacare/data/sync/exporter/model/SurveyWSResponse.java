package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyWSResponse {
    @JsonProperty("UID")
    private String UID;
    private String status;
    private String message;

    public SurveyWSResponse() {
    }

    public SurveyWSResponse(String UID, String status, String message) {
        this.UID = UID;
        this.status = status;
        this.message = message;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getStatus() {
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
