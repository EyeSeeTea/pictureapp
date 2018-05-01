package org.eyeseetea.malariacare.domain.exception;

public class InvalidAgeMonthNumberException extends Exception{
    public static final String ERROR_MESSAGE =
            "Validation info: invalid age month number exception";
    public InvalidAgeMonthNumberException(String detailMessage) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
    }
}
