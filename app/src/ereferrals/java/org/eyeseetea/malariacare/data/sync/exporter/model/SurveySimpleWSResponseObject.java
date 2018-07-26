package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

public class SurveySimpleWSResponseObject {
    private List<SurveySimpleObject> actions;


    public SurveySimpleWSResponseObject() {
        actions = new ArrayList<>();
    }

    public List<SurveySimpleObject> getActions() {
        return actions;
    }

    public void setActions(List<SurveySimpleObject> actions) {
        this.actions = actions;
    }
}

