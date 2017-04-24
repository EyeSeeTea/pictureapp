package org.eyeseetea.malariacare.domain.entity;


import java.util.Date;

public class UserAccount {
    private String userName;
    private String userUid;
    private boolean isDemo;
    private String announcement;
    private Date closeDate;

    public UserAccount(String userName, String userUid, boolean isDemo) {
        this.userName = userName;
        this.isDemo = isDemo;
        this.userUid = userUid;
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

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }
}
