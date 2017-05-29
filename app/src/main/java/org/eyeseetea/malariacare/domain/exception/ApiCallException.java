package org.eyeseetea.malariacare.domain.exception;

public class ApiCallException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Error during a api call. ";

    public ApiCallException(Throwable e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        e.printStackTrace();
    }

    public ApiCallException(String message) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + message);
    }

    public ApiCallException(Exception e, String message) {
        super(ERROR_MESSAGE);
        e.printStackTrace();
        System.out.println(ERROR_MESSAGE + message);
    }
}
