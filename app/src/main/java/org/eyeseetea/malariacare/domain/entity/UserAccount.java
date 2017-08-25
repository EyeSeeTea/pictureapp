package org.eyeseetea.malariacare.domain.entity;


public class UserAccount {
    private String userName;
    private String userUid;
    private boolean isDemo;
    private boolean canAddSurveys = true;

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
}
