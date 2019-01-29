package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .CountriesMetadataVersionsApi;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.CountryMetadataApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IMetadataConfigurationApi {

    @GET("{countriesMetadataVersionsPath}")
    Call<CountriesMetadataVersionsApi> getCountriesMetadataVersions
            (@Path("countriesMetadataVersionsPath") String countriesMetadataVersionsPath);

    @GET("{countryCode}")
    Call<CountryMetadataApi> getCountryMetadata(@Path("countryCode") String countryCode);
}
