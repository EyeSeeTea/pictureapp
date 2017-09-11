package org.eyeseetea.malariacare.domain.exception;

public class ConversionException extends Exception {
    public static String ERROR_MESSAGE = "Exception info: Error during conversion";

    public ConversionException(Exception e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        e.printStackTrace();
    }
}
