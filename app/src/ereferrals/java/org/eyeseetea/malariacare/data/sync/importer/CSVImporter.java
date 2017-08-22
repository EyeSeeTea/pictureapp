package org.eyeseetea.malariacare.data.sync.importer;


import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CSVImporter {
    private static final String TAG = "CSVImporter";

    private Retrofit mRetrofit;
    private CSVImporterRetrofit mCSVImporterRetrofit;
    private final static String BASE_URL = "https://raw.githubusercontent"
            + ".com/manuelplazaspalacio/Test-ereferrals-csvs/master/";

    public CSVImporter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().cache(null).addInterceptor(
                interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build();
        mCSVImporterRetrofit = mRetrofit.create(CSVImporterRetrofit.class);
    }

    public CSVImporter(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().cache(null).addInterceptor(
                interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .build();
        mCSVImporterRetrofit = mRetrofit.create(CSVImporterRetrofit.class);
    }

    public String getCSVVersion() throws IOException {

        Response<ResponseBody> versionString = null;
        try {
            versionString = mCSVImporterRetrofit.getCSVFile(PopulateDB.VERSIONS_CSV).execute();
            String version = versionString.body().string();
            return version;
        } catch (IOException e) {
            Log.e(TAG, "Error downloading csv Version: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }


    public void importCSV(String csvName, CSVImporterCallBack csvImporterCallBack) {
        Response<ResponseBody> csv = null;
        try {
            csv = mCSVImporterRetrofit.getCSVFile(csvName).execute();
            csvImporterCallBack.onSuccess(csv.body().bytes());
        } catch (IOException e) {
            e.printStackTrace();
            csvImporterCallBack.onError(e);
        }


    }

    public interface CSVImporterCallBack<T> {
        void onSuccess(T csvString);

        void onError(Exception e);
    }
}
