package org.eyeseetea.malariacare.domain.exception;

public class ConfigFileObsoleteException extends ApiCallException {
    public static final String ERROR_MESSAGE = "Exception info: Config file is obsolete";

    public ConfigFileObsoleteException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        printStackTrace();
    }
}
