package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SurveyWS {
    @JsonProperty("UID")
    private String UID;
    private List<ValueWS> values;

    public SurveyWS(String UID) {
        this.UID = UID;
    }

    public SurveyWS(String UID, List<ValueWS> values) {
        this.UID = UID;
        this.values = values;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public List<ValueWS> getValues() {
        return values;
    }

    public void setValues(List<ValueWS> values) {
        this.values = values;
    }
}
