package org.eyeseetea.malariacare.domain.exception;

public class InvalidPregnantMonthNumberException extends Exception {
    public static final String ERROR_MESSAGE =
            "Validation info: invalid pregnant month number exception";
    public InvalidPregnantMonthNumberException(String detailMessage) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
