package org.eyeseetea.malariacare.domain.exception;

public class ClosedUserPushException extends Exception {
    public ClosedUserPushException() {
        super("Surveys to push not sent, the user is closed");
    }
}
