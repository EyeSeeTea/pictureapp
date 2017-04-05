package org.eyeseetea.malariacare.domain.exception;

public class LoadingNavigationControllerException extends Exception {
    String error="Error loading the survey navigation controller";
    public LoadingNavigationControllerException(Exception e) {
        super();
        System.out.println(error + e.getMessage());
        e.printStackTrace();
    }
}
