package org.eyeseetea.malariacare.data.sync.importer;


import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CSVImporter {
    private Retrofit mRetrofit;
    private CSVImporterRetrofit mCSVImporterRetrofit;

    public CSVImporter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(
                        "https://raw.githubusercontent"
                                + ".com/manuelplazaspalacio/Test-ereferrals-csvs/master/")
                .client(client)
                .build();
        mCSVImporterRetrofit = mRetrofit.create(CSVImporterRetrofit.class);
    }

    public CSVImporter(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .build();
        mCSVImporterRetrofit = mRetrofit.create(CSVImporterRetrofit.class);
    }

    public void getCSVVersion(CSVImporterCallBack csvImporterCallBack) {

        Response<ResponseBody> versionString = null;
        try {
            versionString = mCSVImporterRetrofit.getVersionCSV().execute();
            String version = versionString.body().string();
            csvImporterCallBack.onSuccess(version);
        } catch (IOException e) {
            e.printStackTrace();
            csvImporterCallBack.onError(e);
        }

    }


    public void importCSV(String csvName, CSVImporterCallBack csvImporterCallBack) {
        Response<ResponseBody> csv = null;
        try {
            csv = getCSVbyName(csvName);
            csvImporterCallBack.onSuccess(csv.body().bytes());
        } catch (IOException e) {
            e.printStackTrace();
            csvImporterCallBack.onError(e);
        }


    }

    private Response<ResponseBody> getCSVbyName(String csvName) throws IOException {
        switch (csvName) {
            case PopulateDB.ANSWERS_CSV:
                return mCSVImporterRetrofit.getAnswersCSV().execute();
            case PopulateDB.PROGRAMS_CSV:
                return mCSVImporterRetrofit.getProgramsCSV().execute();
            case PopulateDB.TABS_CSV:
                return mCSVImporterRetrofit.getTabsCSV().execute();
            case PopulateDB.HEADERS_CSV:
                return mCSVImporterRetrofit.getHeadersCSV().execute();
            case PopulateDB.OPTION_ATTRIBUTES_CSV:
                return mCSVImporterRetrofit.getOptionAttributesCSV().execute();
            case PopulateDB.OPTIONS_CSV:
                return mCSVImporterRetrofit.getOptionsCSV().execute();
            case PopulateDB.QUESTIONS_CSV:
                return mCSVImporterRetrofit.getQuestionsCSV().execute();
            case PopulateDB.QUESTION_RELATIONS_CSV:
                return mCSVImporterRetrofit.getQuestionRelationsCSV().execute();
            case PopulateDB.MATCHES:
                return mCSVImporterRetrofit.getMatchesCSV().execute();
            case PopulateDB.QUESTION_OPTIONS_CSV:
                return mCSVImporterRetrofit.getQuestionOptionsCSV().execute();
            case PopulateDB.QUESTION_THRESHOLDS_CSV:
                return mCSVImporterRetrofit.getQuestionThresholdsCSV().execute();
            case PopulateDB.VERSIONS_CSV:
                return mCSVImporterRetrofit.getVersionCSV().execute();
        }
        return null;
    }


    public interface CSVImporterCallBack<T> {
        void onSuccess(T csvString);

        void onError(Exception e);
    }
}
