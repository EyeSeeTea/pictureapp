package org.eyeseetea.malariacare.domain.exception;

public class WarningException extends Exception {

    public static final String WARNING =
            "Warning: ";

    public WarningException(String message) {
        super(WARNING + message);
    }
}
