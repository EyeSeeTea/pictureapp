package org.eyeseetea.malariacare.domain.exception;

public class DataElementConflictException extends Exception {
    String message;

    public DataElementConflictException(String message) {
        this.message = message;
        System.out.println(DataElementConflictException.class.getName() + " message " + message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
