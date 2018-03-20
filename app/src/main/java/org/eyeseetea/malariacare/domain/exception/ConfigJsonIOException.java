package org.eyeseetea.malariacare.domain.exception;

public class ConfigJsonIOException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Config Json IO exception: ";
    public ConfigJsonIOException(String message) {
        super( ERROR_MESSAGE + message);
        System.out.println(ERROR_MESSAGE + message);
    }

    public ConfigJsonIOException(Exception e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + e.getMessage());
        e.printStackTrace();
    }
}
