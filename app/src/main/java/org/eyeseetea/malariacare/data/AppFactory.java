package org.eyeseetea.malariacare.data;


import android.content.Context;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.POEditorApiClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.LanguageDownloader;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.data.mappers.OptionConvertDomainDBVisitorFromDomainModelToDB;
import org.eyeseetea.malariacare.data.mappers.QuestionConvertDomainDBVisitorFromDomainModelToDB;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class AppFactory {

    private static IConnectivityManager connectivityMN;
    private static IConvertDomainDBVisitor<Question, QuestionDB> questionConverterDomainToDb;
    private static IConvertDomainDBVisitor<Option, OptionDB> optionConverterDomainToDb;

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

    @NonNull
    public static IConvertDomainDBVisitor<Question, QuestionDB> provideQuestionConverter() {


        if (questionConverterDomainToDb == null) {
            questionConverterDomainToDb = new QuestionConvertDomainDBVisitorFromDomainModelToDB(provideOptionConverter());
        }

        return questionConverterDomainToDb;
    }

    @NonNull
    public static IConvertDomainDBVisitor<Option, OptionDB> provideOptionConverter() {


        if (optionConverterDomainToDb == null) {
            optionConverterDomainToDb = new OptionConvertDomainDBVisitorFromDomainModelToDB();
        }
        return optionConverterDomainToDb;
    }
}
