package org.eyeseetea.malariacare.network.retrofit;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    public BasicAuthInterceptor(String credentials) {
        this.credentials = credentials;
    }

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }
}
