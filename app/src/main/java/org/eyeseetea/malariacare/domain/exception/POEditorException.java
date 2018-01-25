package org.eyeseetea.malariacare.domain.exception;

public class POEditorException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: POEditor keys not added in config.json";
    public POEditorException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }

    public POEditorException(String message) {
        super(message);
        System.out.println(ERROR_MESSAGE);
    }
}
