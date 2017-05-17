package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SurveyWSResponseAction {
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";

    private String actionId;
    private String message;
    private String status;
    private String type;
    private SurveyWSResponse response;

    public SurveyWSResponseAction() {
    }

    public SurveyWSResponseAction(String actionId, String message, String status, String type,
            SurveyWSResponse response) {
        this.actionId = actionId;
        this.message = message;
        this.status = status;
        this.type = type;
        this.response = response;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SurveyWSResponse getResponse() {
        return response;
    }

    public void setResponse(SurveyWSResponse response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return status.equals(SUCCESS);
    }
}
