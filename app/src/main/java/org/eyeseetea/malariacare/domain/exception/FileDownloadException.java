package org.eyeseetea.malariacare.domain.exception;


public class FileDownloadException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: FileDownloadException ";

    public FileDownloadException(Throwable t) {
        super(ERROR_MESSAGE);
        t.printStackTrace();
        System.out.println(ERROR_MESSAGE);
    }

    public FileDownloadException(Throwable t, String message) {
        super(ERROR_MESSAGE);
        t.printStackTrace();
        System.out.println(ERROR_MESSAGE + message);
    }
}
