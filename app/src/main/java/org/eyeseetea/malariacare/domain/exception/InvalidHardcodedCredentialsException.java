package org.eyeseetea.malariacare.domain.exception;

public class InvalidHardcodedCredentialsException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Credentials not valid during push";

    public InvalidHardcodedCredentialsException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
