package org.eyeseetea.malariacare.domain.exception;

public class ApiCallException extends Exception {
    public ApiCallException(String message) {
        super(message);
        System.out.println(ApiCallException.class.getName() + message);
    }
}
