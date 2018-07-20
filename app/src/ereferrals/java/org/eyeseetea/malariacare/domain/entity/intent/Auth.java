package org.eyeseetea.malariacare.domain.entity.intent;

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

    public boolean hasAuth() {
        return  ((getUserName()!=null && !getUserName().isEmpty())
                        && (getPassword()!=null && !getPassword().isEmpty()));
    }
}
