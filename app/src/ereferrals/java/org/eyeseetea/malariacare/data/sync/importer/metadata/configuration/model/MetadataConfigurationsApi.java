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

        public boolean visibility = true;

        @JsonProperty("format")
        public PhoneFormat phoneFormat;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Option {

        @JsonProperty("value")
        public String code;

        @JsonProperty("poTerm")
        public String name;

        public List<Rule> rules;

        public static class Rule{
            public String action;
            public Question targetQuestion;
        }

        public boolean hasRules(){
            return rules != null;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhoneFormat{

        public List<AcceptedFormat> accepted;
        public FormatDetails details;


        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AcceptedFormat{
            public String starts;
            public String length;
            public String comment;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class FormatDetails{
            public String trunkPrefix;
            public String dialingCode;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rule {

        public List<Condition> conditions;
        public List<Action> actions;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Condition {

        public Operand left;
        public String operator;
        public Operand right;

    }

    public static class Action {

        public String dataPointRef;
        @JsonProperty("do")
        public String action;

    }

    public static class Operand {

        public String value;

        public String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssuingCapture {
        @JsonProperty("dataValuesSpecs")
        public List<Question> questions;

        @JsonProperty("rulesSpecs")
        public List<Rule> rules;
    }
}
