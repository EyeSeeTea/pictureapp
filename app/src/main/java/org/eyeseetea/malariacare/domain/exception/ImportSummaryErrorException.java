package org.eyeseetea.malariacare.domain.exception;

public class ImportSummaryErrorException extends Exception {
    String message;

    public ImportSummaryErrorException(String message) {
        this.message = message;
        System.out.println(ImportSummaryErrorException.class.getName() + " message " + message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
