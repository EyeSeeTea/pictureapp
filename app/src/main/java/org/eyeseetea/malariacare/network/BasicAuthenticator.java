package org.eyeseetea.malariacare.network;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Basic authenticator required for calls
 */
public class BasicAuthenticator implements Authenticator {

    public final String AUTHORIZATION_HEADER = "Authorization";
    private String credentials;

    BasicAuthenticator() throws ApiCallException {
        try {
            credentials = AuthenticationApiStrategy.getApiCredentials();
        } catch (ConfigJsonIOException e) {
            throw new ApiCallException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiCallException(e);
        }
    }

    public String getCredentials() {
        return credentials;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return response.request().newBuilder().header(AUTHORIZATION_HEADER, credentials).build();
    }
}
