package org.eyeseetea.malariacare.data.sync.importer;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CSVImporterRetrofit {
    @GET("versions.csv")
    Call<ResponseBody> getVersionCSV();
}
