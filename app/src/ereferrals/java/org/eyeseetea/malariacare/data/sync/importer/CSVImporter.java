package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;

import java.io.IOException;

import okhttp3.OkHttpClient;
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
                .baseUrl(PreferencesEReferral.getWSURL())
                .client(client)
                .build();
        mCSVImporterRetrofit = mRetrofit.create(CSVImporterRetrofit.class);
    }

    public void getCSVVersion(CSVImporterCallBack csvImporterCallBack) {

        Response<String> versionString = null;
        try {
            versionString = mCSVImporterRetrofit.getVersionCSV().execute();
        } catch (IOException e) {
            e.printStackTrace();
            csvImporterCallBack.onError(e);
        }
        csvImporterCallBack.onSuccess(versionString.body());

    }


    public interface CSVImporterCallBack {
        void onSuccess(String csvString);

        void onError(Exception e);
    }
}
