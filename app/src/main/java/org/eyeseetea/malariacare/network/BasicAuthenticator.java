package org.eyeseetea.malariacare.network;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;

import java.io.IOException;
import java.net.Proxy;

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
        }
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        return response.request().newBuilder().header(AUTHORIZATION_HEADER, credentials).build();
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }

    public String getCredentials() {
        return credentials;
    }
}
