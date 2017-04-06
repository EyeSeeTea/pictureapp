package org.eyeseetea.malariacare.domain.exception;


public class InvalidPhoneException extends Exception {
    public static final String ERROR_MESSAGE = "Validation info: invalid phone exception";

    public InvalidPhoneException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
