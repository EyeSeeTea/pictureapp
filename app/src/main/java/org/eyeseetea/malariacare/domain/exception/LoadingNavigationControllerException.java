package org.eyeseetea.malariacare.domain.exception;

public class LoadingNavigationControllerException extends Exception {
    public static final String ERROR_MESSAGE =
            "Exception info: Error loading the survey navigation controller";

    public LoadingNavigationControllerException(Exception e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE);
        e.printStackTrace();
    }
}
