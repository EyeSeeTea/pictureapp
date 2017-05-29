package org.eyeseetea.malariacare.domain.exception;

public class LoadingSurveyException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: Error loading new survey, null navigation controller";

    public LoadingSurveyException(Exception e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + e.getMessage());
        e.printStackTrace();
    }
}
