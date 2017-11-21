package org.eyeseetea.malariacare.data.di;


import android.content.Context;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.POEditorApiClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.LanguageDownloader;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Injector {

    private static IConnectivityManager connectivityMN;

    @NonNull
    public static OkHttpClient provideHTTPClientWithLogging() {
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

    @NonNull
    public static ILanguagesClient provideLanguageClient(String projectID, String apiToken) {
        return new POEditorApiClient(projectID, apiToken);

    }

    @NonNull
    public static LanguageDownloader provideLanguageDownloader(ILanguagesClient client,
            IConnectivityManager connectivity) {
        return new LanguageDownloader(client, connectivity);

    }

    @NonNull
    public static IConnectivityManager provideConnectivityMN(final Context ctx) {
        if (connectivityMN == null) {
            connectivityMN = new IConnectivityManager() {
                @Override
                public boolean isDeviceOnline() {
                    return ConnectivityStatus.isConnected(ctx);
                }
            };
        }

        return connectivityMN;
    }


}
