package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.List;

public class SurveyWS {
    private String UID;
    private List<Object> values;

    public SurveyWS(String UID) {
        this.UID = UID;
    }

    public SurveyWS(String UID, List<Object> values) {
        this.UID = UID;
        this.values = values;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
