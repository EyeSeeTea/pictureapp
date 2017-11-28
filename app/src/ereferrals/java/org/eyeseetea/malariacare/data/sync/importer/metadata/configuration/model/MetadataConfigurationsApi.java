package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataConfigurationsApi {

    @JsonProperty("issuing_capture")
    public IssuingCapture issuingCapture;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Question {

        @JsonProperty("dataPointRef")
        public String code;

        @JsonProperty("poTerm")
        public String deName;

        @JsonProperty("controlType")
        public String output;

        @JsonProperty("mandatory")
        public boolean compulsory = true;

        @JsonProperty("options")
        public List<Option> options;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Option {

        @JsonProperty("value")
        public String code;

        @JsonProperty("poTerm")
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssuingCapture {
        @JsonProperty("dataValuesSpecs")
        public List<Question> questions;
    }
}
