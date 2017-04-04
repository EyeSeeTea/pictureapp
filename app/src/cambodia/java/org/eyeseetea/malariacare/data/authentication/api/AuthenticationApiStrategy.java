package org.eyeseetea.malariacare.data.authentication.api;

import org.eyeseetea.malariacare.domain.exception.ConfigJsonInvalidException;

public class AuthenticationApiStrategy extends AuthenticationApi {
    //Authentication
    public static String getApiCredentials() throws ConfigJsonInvalidException {
        return getHardcodedApiCredentials();
    }
}
