package org.eyeseetea.malariacare.domain.exception;

public class EmptyLocationException extends Exception{
    public static final String ERROR_MESSAGE = "Validation: Location is null. ";


    public EmptyLocationException(String message) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + message);
    }
}
