package org.eyeseetea.malariacare.domain.entity;


public class UserAccount {
    private String userName;
    private boolean isDemo;

    public UserAccount(String userName, boolean isDemo) {
        this.userName = userName;
        this.isDemo = isDemo;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isDemo() {
        return isDemo;
    }
}
