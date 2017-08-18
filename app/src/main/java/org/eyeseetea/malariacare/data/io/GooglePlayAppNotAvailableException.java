package org.eyeseetea.malariacare.data.io;

public class GooglePlayAppNotAvailableException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: Google play is not available ";

    public GooglePlayAppNotAvailableException() {
        super(ERROR_MESSAGE);
    }
}