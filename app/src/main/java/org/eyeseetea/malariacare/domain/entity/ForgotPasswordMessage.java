package org.eyeseetea.malariacare.domain.entity;

public class ForgotPasswordMessage {
    private String title;
    private String message;

    public ForgotPasswordMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "ForgotPasswordMessage{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
