package org.eyeseetea.malariacare.domain.exception;


public class ConfigJsonIOException extends Exception {
    public ConfigJsonIOException(String message) {
        super("Config Json IO exception: " + message);
    }
}
