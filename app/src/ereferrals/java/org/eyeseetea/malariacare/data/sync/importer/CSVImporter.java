package org.eyeseetea.malariacare.data.sync.importer;


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


    public interface CSVImporterCallBack {
        void onSuccess(String csvString);

        void onError(Exception e);
    }
}
