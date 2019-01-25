package org.eyeseetea.malariacare.domain.exception;

public class InvalidMetadataException extends ApiCallException {
    public enum TypeOfFailure {
        TRANSLATIONS, CONFIGURATION_FILES,
        TRANSLATIONS_AND_CONFIGURATION_FILES
    }

    TypeOfFailure typeOfFailure;

    public static final String ERROR_MESSAGE = "Verification of metadata after pull was failed";

    public InvalidMetadataException(TypeOfFailure typeOfFailure) {
        super(ERROR_MESSAGE);
        this.typeOfFailure = typeOfFailure;
        System.out.println(ERROR_MESSAGE);
        printStackTrace();
    }

    public TypeOfFailure getTypeOfFailure() {
        return typeOfFailure;
    }
}
