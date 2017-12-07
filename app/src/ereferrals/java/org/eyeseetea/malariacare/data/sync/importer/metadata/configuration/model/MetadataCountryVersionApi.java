package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataCountryVersionApi {

    public List<CountryVersionApi> countriesVersions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryVersionApi {
        public String country;
        public int version;
        public String uid;
    }
}
