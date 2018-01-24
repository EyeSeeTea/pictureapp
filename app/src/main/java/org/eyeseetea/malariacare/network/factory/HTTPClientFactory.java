package org.eyeseetea.malariacare.network.factory;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.eyeseetea.sdk.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HTTPClientFactory {
    @NonNull
    public static OkHttpClient getHTTPClientWithLogging() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        if (BuildConfig.DEBUG) {
            client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        } else {
            client = new OkHttpClient.Builder().build();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return client;
    }
}
