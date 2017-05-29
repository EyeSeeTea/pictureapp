package org.eyeseetea.malariacare.domain.exception;


public class InvalidCredentialsException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Credentials not valid";

    public InvalidCredentialsException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
