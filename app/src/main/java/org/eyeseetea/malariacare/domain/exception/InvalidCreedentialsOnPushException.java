package org.eyeseetea.malariacare.domain.exception;

public class InvalidCreedentialsOnPushException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Credentials not valid during push";

    public InvalidCreedentialsOnPushException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
