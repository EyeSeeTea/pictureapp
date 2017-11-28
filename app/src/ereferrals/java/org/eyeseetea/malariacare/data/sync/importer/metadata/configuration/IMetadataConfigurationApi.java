package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.MetadataConfigurationsApi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IMetadataConfigurationApi {

    @GET("api")
    Call<MetadataConfigurationsApi> getConfiguration();
}
