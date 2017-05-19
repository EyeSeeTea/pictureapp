package org.eyeseetea.malariacare.data.sync.exporter.model;


import java.util.List;

public class SurveyWSResult {
    private SurveyWSResponseOrgUnit response;
    private String version;
    private List<SurveyWSResponseAction> actions;

    public SurveyWSResult() {
    }

    public SurveyWSResult(
            SurveyWSResponseOrgUnit response, String version,
            List<SurveyWSResponseAction> actions) {
        this.response = response;
        this.version = version;
        this.actions = actions;
    }

    public SurveyWSResponseOrgUnit getResponse() {
        return response;
    }

    public void setResponse(
            SurveyWSResponseOrgUnit response) {
        this.response = response;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<SurveyWSResponseAction> getActions() {
        return actions;
    }

    public void setActions(
            List<SurveyWSResponseAction> actions) {
        this.actions = actions;
    }
}
