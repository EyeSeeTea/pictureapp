package org.eyeseetea.malariacare.domain.exception;

public class PullConversionException extends Exception {
    public static String ERROR_MESSAGE = "Error in pull conversion";

    public PullConversionException(Throwable e) {
        super(ERROR_MESSAGE);
        e.printStackTrace();
    }

    public PullConversionException() {
        super(ERROR_MESSAGE);
    }
}
