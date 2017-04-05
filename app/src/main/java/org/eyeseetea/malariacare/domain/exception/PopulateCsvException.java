package org.eyeseetea.malariacare.domain.exception;

import java.io.IOException;

public class PopulateCsvException extends IOException {
    public PopulateCsvException(String message) {
        super("Populate csv IO exception: " + message);
    }
}
