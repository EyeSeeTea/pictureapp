package org.eyeseetea.malariacare.domain.exception;

public class LanguagesDownloadException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: POEditor keys not added in config.json";
    public LanguagesDownloadException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }

    public LanguagesDownloadException(String message) {
        super(message);
        System.out.println(ERROR_MESSAGE);
    }
}
