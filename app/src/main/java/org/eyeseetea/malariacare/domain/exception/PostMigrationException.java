package org.eyeseetea.malariacare.domain.exception;

public class PostMigrationException extends Exception{
    public static final String ERROR_MESSAGE = "Exception info: Error during postMigration: ";

    public PostMigrationException(Throwable e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + e.getMessage());
        e.printStackTrace();
    }
}
