package org.eyeseetea.malariacare.domain.exception;

public class EmptyLocationException extends Exception{
    public static final String ERROR_MESSAGE = "Validation info: Location is null. ";

    public EmptyLocationException(Throwable t) {
        super(ERROR_MESSAGE);
        t.printStackTrace();
        System.out.println(ERROR_MESSAGE);
    }

    public EmptyLocationException(String message) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + message);
    }
}
