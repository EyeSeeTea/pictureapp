package org.eyeseetea.malariacare.domain.exception.push;

public class CheckBanOrgUnitException extends Exception {
    String message;

    public CheckBanOrgUnitException(String message) {
        this.message = message;
        System.out.println(CheckBanOrgUnitException.class.getName() + " message " + message);
    }
    public CheckBanOrgUnitException(Throwable throwable) {
        throwable.printStackTrace();
        this.message = throwable.getMessage();
        System.out.println(CheckBanOrgUnitException.class.getName() + " message " + message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
