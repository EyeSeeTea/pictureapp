package org.eyeseetea.malariacare.domain.entity;

public enum LoginType {

    SOFT(0), FULL(1);

    LoginType(int intType) {
        this.intType = intType;
    }

    private int intType;

    public int getIntType() {
        return intType;
    }

    public static LoginType fromInt(int intLoginType) {
        LoginType loginType = null;

        switch (intLoginType) {

            case 0: {
                loginType = SOFT;
                break;
            }

            case 1: {
                loginType = FULL;
                break;
            }
        }

        return loginType;
    }
}
