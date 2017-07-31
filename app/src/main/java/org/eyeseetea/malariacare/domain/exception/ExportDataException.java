package org.eyeseetea.malariacare.domain.exception;

public class ExportDataException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: Error during the exporting of the metadata ";

    public ExportDataException(Throwable t) {
        super(ERROR_MESSAGE);
        t.printStackTrace();
        System.out.println(ERROR_MESSAGE);
    }

    public ExportDataException(Throwable t, String message) {
        super(ERROR_MESSAGE);
        t.printStackTrace();
        System.out.println(ERROR_MESSAGE + message);
    }
}
