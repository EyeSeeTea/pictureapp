package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.CountryMetadataApi;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.CountriesVersionCodesApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IMetadataConfigurationApi {

    @GET("{countriesCodesPath}")
    Call<CountriesVersionCodesApi> getCountriesCodes(@Path("countriesCodesPath") String countriesCodesPath);

    @GET("{countryCode}")
    Call<CountryMetadataApi> getCountryMetadata(@Path("countryCode") String countryCode);
}
