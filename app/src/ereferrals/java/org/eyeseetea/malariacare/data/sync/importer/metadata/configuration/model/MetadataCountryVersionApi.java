package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataCountryVersionApi {

    public List<CountryVersionApi> countriesVersions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryVersionApi {
        @JsonProperty("cfg_ref")
        public String reference;
        @JsonProperty("ou_code")
        public String country;
        @JsonProperty("cfg_ver")
        public int version;
        @JsonProperty("ou_uid")
        public String uid;
        //TODO: Remove when, the configuration file api whe updated with this parameter
        public String name ="tanzania_program_eref";
    }
}
