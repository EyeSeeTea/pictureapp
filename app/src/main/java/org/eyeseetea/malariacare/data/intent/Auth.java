package org.eyeseetea.malariacare.data.intent;

public class Auth {
    private String userName;
    private String password;

    public Auth() {
    }

    public Auth(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
