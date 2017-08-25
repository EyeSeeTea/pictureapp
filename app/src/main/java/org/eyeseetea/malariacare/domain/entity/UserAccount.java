package org.eyeseetea.malariacare.domain.entity;


public class UserAccount {
    private String userName;
    private String userUid;
    private boolean isDemo;
    private boolean canAddSurveys = true;
    private Phone phone;
    private String appVersion;

    public UserAccount(String userName, String userUid, boolean isDemo) {
        this.userName = userName;
        this.isDemo = isDemo;
        this.userUid = userUid;
    }

    public UserAccount(String userName, String userUid, boolean isDemo, boolean canAddSurveys) {
        this.userName = userName;
        this.isDemo = isDemo;
        this.userUid = userUid;
        this.canAddSurveys = canAddSurveys;
    }

    public UserAccount(String userName, String userUid, boolean isDemo, boolean canAddSurveys,
            Phone phone, String appVersion) {
        this.userName = userName;
        this.userUid = userUid;
        this.isDemo = isDemo;
        this.canAddSurveys = canAddSurveys;
        this.phone = phone;
        this.appVersion = appVersion;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public boolean canAddSurveys() {
        return canAddSurveys;
    }

    public void setCanAddSurveys(boolean canAddSurveys) {
        this.canAddSurveys = canAddSurveys;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
