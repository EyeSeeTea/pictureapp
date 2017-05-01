package org.eyeseetea.malariacare.domain.exception;


public class ConfigJsonNotPresentException extends Exception {
    public ConfigJsonNotPresentException() {
        super("Config Json file not present");
    }
}
