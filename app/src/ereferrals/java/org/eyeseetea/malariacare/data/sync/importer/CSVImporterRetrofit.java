package org.eyeseetea.malariacare.data.sync.importer;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CSVImporterRetrofit {
    @GET("{filename}")
    Call<ResponseBody> getCSVFile(@Path("filename") String filename);
}
