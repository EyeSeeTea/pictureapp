package org.eyeseetea.malariacare.domain.exception;

public class QuestionNotFoundException extends Exception {
    public static final String ERROR_MESSAGE = "Validation info: Question not found during conversion: ";

    public QuestionNotFoundException(String message) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + message);

    }
}
