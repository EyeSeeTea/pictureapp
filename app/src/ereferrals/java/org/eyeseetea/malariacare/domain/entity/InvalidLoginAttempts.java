package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.ActionNotAllowed;

import java.util.Date;

public class InvalidLoginAttempts {

    private static final int NUMBER_ATTEMPTS = 3;
    private static final int DEFAULT_DISABLE_TIME = 30000;

    private int failedLoginAttempts;
    private long enableLoginTime;
    private long disableTime;

    public InvalidLoginAttempts(int failedLoginAttempts, long enableLoginTime) {
        this.failedLoginAttempts = failedLoginAttempts;
        this.enableLoginTime = enableLoginTime;
        disableTime = DEFAULT_DISABLE_TIME;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public long getEnableLoginTime() {
        return enableLoginTime;
    }

    public void setEnableLoginTime(long enableLoginTime) {
        this.enableLoginTime = enableLoginTime;
    }

    public void addFailedAttempts() throws ActionNotAllowed {
        if (isLoginEnabled()) {
            failedLoginAttempts++;
            if (failedLoginAttempts >= NUMBER_ATTEMPTS) {
                enableLoginTime = new Date().getTime() + disableTime;
                failedLoginAttempts = 0;
            }
        } else {
            throw new ActionNotAllowed("Add new attempt if login is disable is not allowed.");
        }
    }

    public boolean isLoginEnabled() {
        return enableLoginTime < new Date().getTime();
    }

    public long getDisableTime() {
        return disableTime;
    }

    public void setDisableTime(long disableTime) {
        this.disableTime = disableTime;
    }
}
