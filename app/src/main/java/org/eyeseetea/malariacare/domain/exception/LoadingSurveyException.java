package org.eyeseetea.malariacare.domain.exception;

public class LoadingSurveyException extends Exception {
        String error="Error loading new survey, null navigation controller";
        public LoadingSurveyException(Exception e) {
            super();
            System.out.println(error + e.getMessage());
            e.printStackTrace();
        }
    }
