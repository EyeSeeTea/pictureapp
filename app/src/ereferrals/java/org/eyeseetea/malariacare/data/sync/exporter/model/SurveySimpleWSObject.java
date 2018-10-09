package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class SurveySimpleWSObject {
    private List<Id> actions;


    public SurveySimpleWSObject() {
        actions = new ArrayList<>();
    }

    public List<Id> getActions() {
        return actions;
    }

    public void setActions(List<Id> actions) {
        this.actions = actions;
    }
}

