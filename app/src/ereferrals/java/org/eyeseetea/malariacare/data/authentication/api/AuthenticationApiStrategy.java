package org.eyeseetea.malariacare.data.authentication.api;

import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;

public class AuthenticationApiStrategy extends AuthenticationApi {
    //Authentication
    public static String getApiCredentials() throws ConfigJsonIOException  {
        return getApiCredentialsFromLogin();
    }
}
