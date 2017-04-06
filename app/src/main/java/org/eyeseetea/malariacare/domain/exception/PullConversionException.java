package org.eyeseetea.malariacare.domain.exception;

public class PullConversionException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Error in pull conversion: ";

    public PullConversionException(Throwable e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + e.getMessage());
        e.printStackTrace();
    }
}
