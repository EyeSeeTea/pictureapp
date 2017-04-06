package org.eyeseetea.malariacare.domain.exception;

public class ConvertedEventsToPushNotFoundException extends Exception{
    public static String ERROR_MESSAGE = "Exception info: Converted Events to push not found";

    public ConvertedEventsToPushNotFoundException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
