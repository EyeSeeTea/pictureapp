package org.eyeseetea.malariacare.network;

import android.util.Log;

import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.network.factory.UnsafeOkHttpsClientFactory;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerApiCallExecution {

    /**
     * MediaType always json + utf8
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Call to DHIS Server
     */
    static Response executeCall(JSONObject data, String url, String method)
            throws ApiCallException {
        final String DHIS_URL = url;
        Log.d(method, DHIS_URL);

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator();

        OkHttpClient client = UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient(basicAuthenticator);

        Request.Builder builder = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER,
                        basicAuthenticator.getCredentials())
                .url(DHIS_URL);

        switch (method) {
            case "POST":
                RequestBody postBody = RequestBody.create(JSON, data.toString());
                builder.post(postBody);
                break;
            case "PUT":
                RequestBody putBody = RequestBody.create(JSON, data.toString());
                builder.put(putBody);
                break;
            case "PATCH":
                RequestBody patchBody = RequestBody.create(JSON, data.toString());
                builder.patch(patchBody);
                break;
            case "GET":
                builder.get();
                break;
        }

        Request request = builder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException ex) {
            throw new ApiCallException(ex);
        }
        return response;
    }
}

