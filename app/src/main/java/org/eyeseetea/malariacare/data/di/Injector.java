package org.eyeseetea.malariacare.data.di;


import android.content.Context;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.POEditorApiClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.LanguageDownloader;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.converters.IConverter;
import org.eyeseetea.malariacare.data.database.converts.OptionConverterFromDomainModelToDB;
import org.eyeseetea.malariacare.data.database.converts.QuestionConverterFromDomainModelToDB;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Injector {

    private static IConnectivityManager connectivityMN;
    private static IConverter<Question, QuestionDB> questionConverterDomainToDb;
    private static IConverter<Option, OptionDB> optionConverterDomainToDb;

    @NonNull
    public static OkHttpClient provideHTTPClientWithLogging() {
        return provideHTTPClientWithLoggingWith();
    }

    @NonNull
    public static OkHttpClient provideHTTPClientWithLoggingWith(Interceptor... interceptors) {
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
    public static BasicAuthInterceptor provideAuthenticationInterceptor() throws Exception {
        return new BasicAuthInterceptor(AuthenticationApiStrategy.getApiCredentials());
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

    @NonNull
    public static IMetadataConfigurationDataSource provideMetadataConfigurationDataSource(
            BasicAuthInterceptor basicAuthInterceptor)
            throws Exception {
        return new MetadataConfigurationApiClient(PreferencesState.getInstance().getDhisURL(),
                basicAuthInterceptor);
    }

    @NonNull
    public static IConverter<Question, QuestionDB> provideQuestionConverter() {


        if (questionConverterDomainToDb == null) {
            questionConverterDomainToDb = new QuestionConverterFromDomainModelToDB(
                    provideOptionConverter());
        }

        return questionConverterDomainToDb;
    }

    @NonNull
    public static IConverter<Option, OptionDB> provideOptionConverter() {


        if (optionConverterDomainToDb == null) {
            optionConverterDomainToDb = new OptionConverterFromDomainModelToDB();
        }

        return optionConverterDomainToDb;
    }


}
