package org.eyeseetea.malariacare.domain.exception;


public class NetworkException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: Network not available";

    public NetworkException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
