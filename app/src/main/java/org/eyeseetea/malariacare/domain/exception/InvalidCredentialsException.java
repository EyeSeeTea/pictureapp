package org.eyeseetea.malariacare.domain.exception;


public class InvalidCredentialsException extends Exception {
    public static String ERROR_MESSAGE = "Exception: Credentials not valid";

    public InvalidCredentialsException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
