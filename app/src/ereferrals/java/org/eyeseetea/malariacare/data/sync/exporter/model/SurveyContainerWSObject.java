package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class SurveyContainerWSObject {
    private String connectVersion;
    private String source;
    private String userName;
    private String password;
    private List<SurveySendAction> actions;
    private String androidInfo;
    private int configVersion;
    private String connectCode;
    private String configDateTime;
    private SettingsSummary settingsSummary;


    public SurveyContainerWSObject() {
        actions = new ArrayList<>();
    }

    public SurveyContainerWSObject(String connectVersion, String source, String userName,
            String password, String androidInfo, int configVersion, String connectCode,
            String configDateTime, SettingsSummary settingsSummary) {
        this.connectVersion = connectVersion;
        this.source = source;
        this.userName = userName;
        this.password = password;
        actions = new ArrayList<>();
        this.androidInfo = androidInfo;
        this.configVersion = configVersion;
        this.connectVersion = connectCode;
        this.configDateTime = configDateTime;
        this.settingsSummary = settingsSummary;
    }

    public String getConnectVersion() {
        return connectVersion;
    }

    public void setConnectVersion(String connectVersion) {
        this.connectVersion = connectVersion;
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

    public String getAndroidInfo() {
        return androidInfo;
    }

    public void setAndroidInfo(String androidInfo) {
        this.androidInfo = androidInfo;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public String getConnectCode() {
        return connectCode;
    }

    public void setConnectCode(String connectCode) {
        this.connectCode = connectCode;
    }

    public String getConfigDateTime() {
        return configDateTime;
    }

    public void setConfigDateTime(String configDateTime) {
        this.configDateTime = configDateTime;
    }

    public SettingsSummary getSettingsSummary() {
        return settingsSummary;
    }

    public void setSettingsSummary(
            SettingsSummary settingsSummary) {
        this.settingsSummary = settingsSummary;
    }
}

