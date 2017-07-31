package org.eyeseetea.malariacare.domain.exception;


public class InvalidPositiveNumberException extends Exception {
    public static final String ERROR_MESSAGE = "Validation info: invalid positive number exception";

    public InvalidPositiveNumberException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
