package org.eyeseetea.malariacare.data.authentication.api;

import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.LanguagesDownloadException;

public class AuthenticationApiStrategy extends AuthenticationApi {
    //Authentication
    public static String getApiCredentials()
            throws ConfigJsonIOException, LanguagesDownloadException {
        return getHardcodedApiCredentials();
    }
}
