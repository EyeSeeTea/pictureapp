package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryMetadataApi {

    @JsonProperty("issuing_capture")
    public IssuingCapture issuingCapture;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Question {
        public static final String TYPE_DATA_POINT_REF ="dataPointRef";
        public static final String TYPE_VALUE ="value";

        public static final String DISPLAY_PRIORITY_VISIBLE ="VISIBLE";
        public static final String DISPLAY_PRIORITY_INVISIBLE ="INVISIBLE";
        public static final String DISPLAY_PRIORITY_IMPORTANT ="IMPORTANT";

        public static final String OPERATOR_EQUAL ="==";
        public static final String OPERATOR_GREATER_THAN =">";
        public static final String OPERATOR_GREATER_OR_EQUAL_THAN =">=";
        public static final String OPERATOR_LESS_THAN ="<";
        public static final String OPERATOR_LESS_OR_EQUAL_THAN ="<=";
        public static final String ACTION_SHOW ="SHOW";

        public static final String CONTROL_TYPE_SHORT_TEXT = "SHORT_TEXT";
        public static final String CONTROL_TYPE_PHONE = "PHONE_NUMBER";
        public static final String CONTROL_TYPE_DROPDOWN_LIST = "DROPDOWN_LIST";
        public static final String CONTROL_TYPE_YEAR = "YEAR";
        public static final String CONTROL_TYPE_DATE = "DATE";
        public static final String CONTROL_LONG_TEXT = "LONG_TEXT";
        public static final String CONTROL_INT = "INT";
        public static final String CONTROL_POSITIVE_INT = "POSITIVE_INT";
        public static final String CONTROL_PREGNANT_MONTH_INT = "PREGNANT_MONTH_INT";
        public static final String CONTROL_RADIO_GROUP_HORIZONTAL = "RADIO_GROUP_HORIZONTAL";
        public static final String CONTROL_QUESTION_LABEL = "QUESTION_LABEL";
        public static final String CONTROL_SWITCH_BUTTON = "SWITCH_BUTTON";
        public static final String CONTROL_TYPE_AUTOCOMPLETE_TEXT = "AUTOCOMPLETE";


        @JsonProperty("dataPointRef")
        public String code;

        @JsonProperty("poTerm")
        public String deName;

        @JsonProperty("controlType")
        public String output;

        @JsonProperty("validationRegex")
        public String validationRegex;

        @JsonProperty("validationPoTerm")
        public String validationPoTerm;

        @JsonProperty("defaultValue")
        public String defaultValue;

        @JsonProperty("mandatory")
        public boolean compulsory = true;

        @JsonProperty("options")
        public List<Option> options;

        public String queueDisplayPriority = DISPLAY_PRIORITY_VISIBLE;

        @JsonProperty("format")
        public PhoneFormat phoneFormat;

        public List<Rule> rules;

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
