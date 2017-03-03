package org.eyeseetea.malariacare.domain.usecase.api;

public class AuthenticationApiStrategy extends AuthenticationApi {
    //Authentication
    public static String getApiCredentials() {
        return getHardcodedApiCredentials();
    }
}
