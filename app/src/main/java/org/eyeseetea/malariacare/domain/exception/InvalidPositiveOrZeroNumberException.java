package org.eyeseetea.malariacare.domain.exception;


public class InvalidPositiveOrZeroNumberException extends Exception {
    public static final String ERROR_MESSAGE =
            "Validation info: invalid positive or zero number exception";

    public InvalidPositiveOrZeroNumberException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
