package org.eyeseetea.malariacare.domain.exception;

public class ConvertedEventsToPushNotFoundException extends Exception{
    public static String ERROR_MESSAGE = "Exception: Converted Events to push not found";

    public ConvertedEventsToPushNotFoundException() {
        super(ERROR_MESSAGE);
        System.out.printf(ERROR_MESSAGE);
    }
}
