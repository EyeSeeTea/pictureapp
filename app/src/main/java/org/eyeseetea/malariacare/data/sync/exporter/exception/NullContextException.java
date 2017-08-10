package org.eyeseetea.malariacare.data.sync.exporter.exception;

public class NullContextException extends Exception {
    public static String ERROR_MESSAGE = "Exception info: Error null context during conversion";

    public NullContextException() {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }

    public NullContextException(Exception e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        e.printStackTrace();
    }
}
