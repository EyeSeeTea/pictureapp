package org.eyeseetea.malariacare.network.factory;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.eyeseetea.sdk.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HTTPClientFactory {
    @NonNull
    public static OkHttpClient getHTTPClientWithLogging() {
        return getHTTPClientWithLoggingWith();
    }

    @NonNull
    public static OkHttpClient getHTTPClientWithLoggingWith(Interceptor... interceptors) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        for (Interceptor inter : interceptors) {
            clientBuilder.addInterceptor(inter);
        }

        if (BuildConfig.DEBUG) {

            client = clientBuilder.addInterceptor(interceptor).build();
        } else {
            client = clientBuilder.build();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return client;
    }

    @NonNull
    public static BasicAuthInterceptor getAuthenticationInterceptor() throws Exception {
        return new BasicAuthInterceptor(AuthenticationApiStrategy.getApiCredentials());
    }

}
