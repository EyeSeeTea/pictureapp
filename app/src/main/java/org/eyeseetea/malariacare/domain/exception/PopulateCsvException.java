package org.eyeseetea.malariacare.domain.exception;

import java.io.IOException;

public class PopulateCsvException extends IOException {
    public static final String ERROR_MESSAGE =
            "Exception info: Populating csv";

    public PopulateCsvException(Exception ex) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
