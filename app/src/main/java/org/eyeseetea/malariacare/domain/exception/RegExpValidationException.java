package org.eyeseetea.malariacare.domain.exception;

public class RegExpValidationException extends Exception{
    public static final String ERROR_MESSAGE =
            "Validation info: error with the reg exp and value: ";
    public RegExpValidationException(String detailMessage) {
        super(ERROR_MESSAGE+detailMessage);
        System.out.println(ERROR_MESSAGE+detailMessage);
    }
}
