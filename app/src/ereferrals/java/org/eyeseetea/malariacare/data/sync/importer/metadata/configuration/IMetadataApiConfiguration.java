package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.MetadataApiConfigurations;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IMetadataApiConfiguration {

    @GET("api")
    Call<MetadataApiConfigurations> getConfiguration();
}
