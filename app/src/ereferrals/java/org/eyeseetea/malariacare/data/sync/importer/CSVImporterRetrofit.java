package org.eyeseetea.malariacare.data.sync.importer;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CSVImporterRetrofit {
    @GET("version")
    Call<String> getVersionCSV();
}
