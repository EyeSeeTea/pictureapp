package org.eyeseetea.malariacare.domain.exception;

public class MigrateMigrationException  extends Exception{
    public static final String ERROR_MESSAGE = "Exception info: Error during migration migrate: ";

    public MigrateMigrationException(Throwable e) {
        super(ERROR_MESSAGE);
        System.out.println(ERROR_MESSAGE + e.getMessage());
        e.printStackTrace();
    }
}