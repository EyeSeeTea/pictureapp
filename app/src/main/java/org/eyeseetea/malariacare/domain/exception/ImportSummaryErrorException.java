package org.eyeseetea.malariacare.domain.exception;

public class ImportSummaryErrorException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: ImportSummary Exception: ";
    String message;

    public ImportSummaryErrorException(String message) {
        super(ERROR_MESSAGE);
        this.message = message;
        System.out.println(ERROR_MESSAGE + " message " + message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
