package org.eyeseetea.malariacare.domain.exception;

public class ClosedUserPushException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: The user is closed, Surveys not sent";
    public ClosedUserPushException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
