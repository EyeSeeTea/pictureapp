package org.eyeseetea.malariacare.domain.exception;


public class ConfigJsonInvalidException extends Exception {
    public ConfigJsonInvalidException(String message) {
        super("Config Json file is invalid " + message);
    }
}
