package org.eyeseetea.malariacare.domain.entity;

public class ApiStatus {
    private boolean available;
    private String message;

    public ApiStatus(boolean available, String message) {
        this.available = available;
        this.message = message;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getMessage() {
        return message;
    }
}
