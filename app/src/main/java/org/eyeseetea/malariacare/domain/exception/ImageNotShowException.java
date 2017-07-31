package org.eyeseetea.malariacare.domain.exception;

public class ImageNotShowException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Error loading image";

    public ImageNotShowException(Throwable t, String message) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        t.printStackTrace();
    }
}
