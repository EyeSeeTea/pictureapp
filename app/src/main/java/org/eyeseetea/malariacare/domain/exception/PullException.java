package org.eyeseetea.malariacare.domain.exception;

import java.io.IOException;

public class PullException extends Exception {
    public static final String ERROR_MESSAGE = "Exception info: Metadata pull null exception";

    public PullException(Throwable e) {
        super(ERROR_MESSAGE);
        e.printStackTrace();
    }

    public PullException() {
        super(ERROR_MESSAGE);
    }
}
