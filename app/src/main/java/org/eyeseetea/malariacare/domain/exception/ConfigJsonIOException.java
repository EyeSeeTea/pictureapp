package org.eyeseetea.malariacare.domain.exception;

public class ConfigJsonIOException extends Exception {
    public static String ERROR_MESSAGE = "Config Json IO exception: ";
    public ConfigJsonIOException(String message) {
        super( ERROR_MESSAGE + message);
        System.out.println(ERROR_MESSAGE + message);
    }
}
