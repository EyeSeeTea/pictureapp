package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class SurveyContainerWSObject {
    private String version;
    private String source;
    private String userName;
    private String password;
    private List<SurveySendAction> actions;

    public SurveyContainerWSObject() {
        actions = new ArrayList<>();
    }

    public SurveyContainerWSObject(String version, String source, String userName,
            String password) {
        this.version = version;
        this.source = source;
        this.userName = userName;
        this.password = password;
        actions = new ArrayList<>();

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SurveySendAction> getActions() {
        return actions;
    }

    public void setActions(List<SurveySendAction> actions) {
        this.actions = actions;
    }
}

