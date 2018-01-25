package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.converter.PhoneFormatToDomainVisitor;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataCountryVersionApi;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.network.factory.HTTPClientFactory;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MetadataConfigurationApiClient implements IMetadataConfigurationDataSource {

    private IMetadataConfigurationApi configurationApi;

    public MetadataConfigurationApiClient(String url, BasicAuthInterceptor basicAuthInterceptor)
            throws Exception {

        OkHttpClient client = HTTPClientFactory.getHTTPClientWithLoggingWith(basicAuthInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .baseUrl(url)
                .build();

        configurationApi = retrofit.create(IMetadataConfigurationApi.class);
    }

    @Override
    public List<Question> getQuestionsByCountryCode(String countryCode) throws Exception {

        MetadataConfigurationConverterApiModelToDomain
                converter = new MetadataConfigurationConverterApiModelToDomain();

        List<MetadataConfigurationsApi.Question> apiQuestions = new ArrayList<>();

        List<MetadataConfigurationsApi.Rule> apiRules;


        MetadataConfigurationsApi metadata = getMetadataConfigurationsApi(countryCode);


        if (isApiQuestionNotNull(metadata)) {
            apiQuestions = metadata.issuingCapture.questions;

            apiRules = metadata.issuingCapture.rules;

            assignRulesToQuestions(apiRules, apiQuestions);
        }

        return converter.convertToDomainQuestionsFrom(apiQuestions);
    }

    @NonNull
    private MetadataConfigurationsApi getMetadataConfigurationsApi(@NonNull String countryCode)
            throws Exception {

        Response<MetadataConfigurationsApi> response;
        try {

            response = configurationApi.getConfiguration(countryCode).execute();

        } catch (Exception error) {
            throw new ApiCallException(error);
        }


        return getResultsOrThrowException(response);
    }

    @Override
    public List<Configuration.CountryVersion> getCountriesVersions() throws Exception {

        Response<MetadataCountryVersionApi> response =
                configurationApi.getCountriesVersions().execute();

        MetadataCountryVersionApi metadata = getResultsOrThrowException(response);

        return convertToDomainCountryVersion(metadata.countriesVersions);
    }

    @NonNull
    private List<Configuration.CountryVersion> convertToDomainCountryVersion(
            @NonNull List<MetadataCountryVersionApi.CountryVersionApi> countriesVersionsApi) {

        List<Configuration.CountryVersion> domainCountriesVersions = new ArrayList<>();

        for (MetadataCountryVersionApi.CountryVersionApi countryVersionApi : countriesVersionsApi) {

            Configuration.CountryVersion domain =
                    Configuration.CountryVersion.newBuilder()
                            .country(countryVersionApi.country)
                            .name(countryVersionApi.name)
                            .version(countryVersionApi.version)
                            .reference(countryVersionApi.reference)
                            .uid(countryVersionApi.uid)
                            .lastUpdate(new Date())
                            .build();

            domainCountriesVersions.add(domain);
        }
        return domainCountriesVersions;
    }

    private void assignRulesToQuestions(
            @NonNull List<MetadataConfigurationsApi.Rule> apiRules,
            @NonNull List<MetadataConfigurationsApi.Question> apiQuestions) {

        Map<String, MetadataConfigurationsApi.Question> mapQuestionsByCode =
                mapQuestionsByCode(apiQuestions);

        for (MetadataConfigurationsApi.Rule rule : apiRules) {

            assignRuleTo(rule, mapQuestionsByCode);
        }
    }

    @NonNull
    private MetadataConfigurationsApi.Option.Rule newRuleOptionFrom(
            @NonNull MetadataConfigurationsApi.Condition condition,
            @NonNull MetadataConfigurationsApi.Question targetQuestion) {


        MetadataConfigurationsApi.Option.Rule newOptionRule =
                new MetadataConfigurationsApi.Option.Rule();

        newOptionRule.action = condition.operator;
        newOptionRule.targetQuestion = targetQuestion;

        newOptionRule.targetQuestion.visibility = !condition.operator.equals("SHOW");

        return newOptionRule;
    }

    private void assignRuleTo(
            @NonNull MetadataConfigurationsApi.Rule apiRule,
            @NonNull Map<String, MetadataConfigurationsApi.Question> questionsByCode) {

        List<MetadataConfigurationsApi.Condition> conditions = apiRule.conditions;

        for (int i = 0; i < conditions.size(); i++) {
            MetadataConfigurationsApi.Condition condition = conditions.get(i);

            String questionCodeWithRule = condition.left.value;
            String optionCodeWithRule = condition.right.value;

            String targetQuestionCode = apiRule.actions.get(i).dataPointRef;


            MetadataConfigurationsApi.Question questionWithRule = questionsByCode.get(
                    questionCodeWithRule);

            MetadataConfigurationsApi.Option optionWithoutRule = findOptionBy(optionCodeWithRule,
                    questionWithRule);


            if (optionWithoutRule != null) {

                MetadataConfigurationsApi.Question targetQuestion = questionsByCode.get(
                        targetQuestionCode);

                assignRuleTo(optionWithoutRule, condition, targetQuestion);
            }

        }


    }

    private void assignRuleTo(@NonNull MetadataConfigurationsApi.Option option,
            @NonNull MetadataConfigurationsApi.Condition condition,
            @NonNull MetadataConfigurationsApi.Question targetQuestion) {

        MetadataConfigurationsApi.Option.Rule newOptRule = newRuleOptionFrom(condition,
                targetQuestion);

        if (option.rules == null) {
            option.rules = new ArrayList<>();

        }
        option.rules.add(newOptRule);
    }

    @Nullable
    private MetadataConfigurationsApi.Option findOptionBy(@NonNull String optionCode,
            @NonNull MetadataConfigurationsApi.Question question) {

        MetadataConfigurationsApi.Option foundOption = null;
        if (question.options != null) {
            for (MetadataConfigurationsApi.Option option : question.options) {

                if (option.code.equals(optionCode)) {

                    foundOption = option;
                    break;
                }
            }
        }
        return foundOption;
    }


    private boolean isApiQuestionNotNull(@NonNull MetadataConfigurationsApi metadata) {
        return metadata.issuingCapture != null &&
                metadata.issuingCapture.questions != null;
    }

    @NonNull
    private Map<String, MetadataConfigurationsApi.Question> mapQuestionsByCode(
            @NonNull List<MetadataConfigurationsApi.Question> questions) {

        Map<String, MetadataConfigurationsApi.Question> map = new HashMap<>();

        for (MetadataConfigurationsApi.Question i : questions) map.put(i.code, i);

        return map;
    }

    @NonNull
    private <E> E getResultsOrThrowException(
            Response<E> response)
            throws Exception {

        if (response.isSuccessful()) {
            return response.body();
        } else {
            String error = response.errorBody().string() + "Http Code: " + response.code();

            throw new ApiCallException(error);
        }
    }

    private class MetadataConfigurationConverterApiModelToDomain {

        private final String CONTROL_TYPE_SHORT_TEXT = "SHORT_TEXT";
        private final String CONTROL_TYPE_PHONE = "PHONE_NUMBER";
        private final String CONTROL_TYPE_DROPDOWN_LIST = "DROPDOWN_LIST";
        private final String CONTROL_TYPE_YEAR = "YEAR";
        private final String CONTROL_TYPE_DATE = "DATE";
        private final String CONTROL_LONG_TEXT = "LONG_TEXT";
        private final String CONTROL_INT = "INT";
        private final String CONTROL_POSITIVE_INT = "POSITIVE_INT";
        private final String CONTROL_PREGNANT_MONTH_INT = "PREGNANT_MONTH_INT";
        private final String CONTROL_RADIO_GROUP_HORIZONTAL = "RADIO_GROUP_HORIZONTAL";
        private final String CONTROL_QUESTION_LABEL = "QUESTION_LABEL";
        private final String CONTROL_SWITCH_BUTTON = "SWITCH_BUTTON";
        private final PhoneFormatToDomainVisitor phoneFormatConverter =
                new PhoneFormatToDomainVisitor();

        Map<String, Question> mapDomainQuestionsByCode = new HashMap<>();
        Map<String, List<Option>> mapDomainOptionsWithRuleByQuestionCodes = new HashMap<>();
        Map<String, List<MetadataConfigurationsApi.Option>> mapApiOptionsWithRulesByQuestionCodes =
                new HashMap<>();


        @NonNull
        private List<Question> convertToDomainQuestionsFrom(
                @NonNull List<MetadataConfigurationsApi.Question> apiQuestions) {

            List<Question> domainQuestions = new ArrayList<>();

            boolean isImportantQuestionSelected = false;
            for (int questionIndex = 0; questionIndex < apiQuestions.size(); questionIndex++) {

                MetadataConfigurationsApi.Question apiQuestion = apiQuestions.get(questionIndex);

                Question domainQuestion = convertToDomainQuestionFrom(apiQuestion, questionIndex,
                        isImportantQuestionSelected);

                domainQuestions.add(domainQuestion);
                mapDomainQuestionsByCode.put(domainQuestion.getCode(), domainQuestion);
                isImportantQuestionSelected = isImportantQuestion(domainQuestion);

            }

            assignRulesToQuestions();

            return domainQuestions;
        }


        private boolean isImportantQuestion(Question question) {
            return question.getVisibility() == Question.Visibility.IMPORTANT;
        }

        private void assignRulesToQuestions() {

            Set<String> questionCodesWithRules = mapApiOptionsWithRulesByQuestionCodes.keySet();

            for (String questionCode : questionCodesWithRules) {

                List<Option> domainOptions = mapDomainOptionsWithRuleByQuestionCodes.get(
                        questionCode);

                List<MetadataConfigurationsApi.Option> apiOptions =
                        mapApiOptionsWithRulesByQuestionCodes.get(questionCode);

                addDomainRulesTo(domainOptions, apiOptions);
            }
        }

        private void addDomainRulesTo(List<Option> domainOptions,
                List<MetadataConfigurationsApi.Option> apiOptions) {
            for (int i = 0; i < domainOptions.size(); i++) {

                Option domainOption = domainOptions.get(i);
                MetadataConfigurationsApi.Option aipOption = apiOptions.get(i);


                addDomainRulesTo(domainOption, aipOption);
            }
        }

        private void addDomainRulesTo(Option domainOption,
                MetadataConfigurationsApi.Option aipOption) {
            List<Option.Rule> domainRules = new ArrayList<>();

            for (MetadataConfigurationsApi.Option.Rule ruleOptionApi : aipOption.rules) {

                Question targetQuestion = mapDomainQuestionsByCode.get(
                        ruleOptionApi.targetQuestion.code);

                Option.Rule newRule =
                        Option.Rule.newBuilder()
                                .action(Option.Rule.Action.SHOW)
                                .operator(Option.Rule.Operator.EQUAL)
                                .actionSubject(targetQuestion)
                                .build();

                domainRules.add(newRule);
            }

            domainOption.setRules(domainRules);
        }


        @NonNull
        private Question convertToDomainQuestionFrom(
                @NonNull MetadataConfigurationsApi.Question apiQuestion, int index,
                boolean isImportantQuestionSelected) {


            return Question.newBuilder()
                    .code(apiQuestion.code)
                    .uid(apiQuestion.code)
                    .phoneFormat(convertToDomainPhoneFormatFrom(apiQuestion.phoneFormat))
                    .name(apiQuestion.deName)
                    .index(index)
                    .type(convertToDomainQuestionTypeFrom(apiQuestion.output))
                    .visibility(getVisibilityFrom(apiQuestion, isImportantQuestionSelected))
                    .options(convertToDomainOptionsFrom(apiQuestion.options, apiQuestion))
                    .compulsory(apiQuestion.compulsory)
                    .build();
        }

        @NonNull
        private Question.Visibility getVisibilityFrom(
                @NonNull MetadataConfigurationsApi.Question
                        apiQuestion, boolean isImportantSelected) {

            if (!isImportantSelected && apiQuestion.visibility) {
                return Question.Visibility.IMPORTANT;
            }

            return (apiQuestion.visibility)
                    ? Question.Visibility.VISIBLE
                    : Question.Visibility.INVISIBLE;
        }

        @Nullable
        private PhoneFormat convertToDomainPhoneFormatFrom(
                MetadataConfigurationsApi.PhoneFormat apiPhoneFormat) {

            if (apiPhoneFormat != null) {
                return phoneFormatConverter.convert(apiPhoneFormat);
            }

            return null;
        }


        @NonNull
        private Question.Type convertToDomainQuestionTypeFrom(@NonNull String apiControlType) {
            Question.Type questionType = Question.Type.SHORT_TEXT;

            switch (apiControlType) {

                case CONTROL_TYPE_SHORT_TEXT:
                    questionType = Question.Type.SHORT_TEXT;
                    break;


                case CONTROL_TYPE_PHONE:
                    questionType = Question.Type.PHONE;
                    break;

                case CONTROL_TYPE_DROPDOWN_LIST:
                    questionType = Question.Type.DROPDOWN_LIST;
                    break;


                case CONTROL_TYPE_YEAR:
                    questionType = Question.Type.YEAR;
                    break;

                case CONTROL_TYPE_DATE:
                    questionType = Question.Type.DATE;
                    break;

                case CONTROL_LONG_TEXT:
                    questionType = Question.Type.LONG_TEXT;
                    break;

                case CONTROL_POSITIVE_INT:
                    questionType = Question.Type.POSITIVE_INT;
                    break;

                case CONTROL_INT:
                    questionType = Question.Type.INT;
                    break;

                case CONTROL_PREGNANT_MONTH_INT:
                    questionType = Question.Type.PREGNANT_MONTH;
                    break;

                case CONTROL_RADIO_GROUP_HORIZONTAL:
                    questionType = Question.Type.RADIO_GROUP_HORIZONTAL;
                    break;

                case CONTROL_QUESTION_LABEL:
                    questionType = Question.Type.QUESTION_LABEL;
                    break;

                case CONTROL_SWITCH_BUTTON:
                    questionType = Question.Type.SWITCH_BUTTON;
                    break;

            }

            return questionType;
        }

        @NonNull
        private List<Option> convertToDomainOptionsFrom(
                @NonNull List<MetadataConfigurationsApi.Option> apiOptions,
                @NonNull MetadataConfigurationsApi.Question apiQuestion) {

            List<Option> domainOptions = new ArrayList<>();

            //noinspection ConstantConditions
            if (apiOptions == null) return domainOptions;


            for (MetadataConfigurationsApi.Option apiOption : apiOptions) {

                Option domainOption = convertToDomainOptionFrom(apiOption);
                domainOptions.add(domainOption);

                if (apiOption.hasRules()) {
                    //This Rules are going to be added after all the questions
                    //has been converted to domain questions.
                    //Because there rules target to question that might now be converted yet
                    addToPendingRules(apiQuestion, apiOption, domainOption);
                }
            }

            return domainOptions;
        }

        private void addToPendingRules(@NonNull MetadataConfigurationsApi.Question apiQuestion,
                @NonNull MetadataConfigurationsApi.Option apiOption, @NonNull Option domainOption) {

            addItemToListOf(mapDomainOptionsWithRuleByQuestionCodes, apiQuestion.code,
                    domainOption);
            addItemToListOf(mapApiOptionsWithRulesByQuestionCodes, apiQuestion.code, apiOption);
        }

        private <E> void addItemToListOf(@NonNull Map<String, List<E>> map, @NonNull String key,
                E val) {
            List<E> mapList;

            if (map.containsKey(key)) {
                mapList = map.get(key);
            } else {
                mapList = new ArrayList<>();
                map.put(key, mapList);
            }
            mapList.add(val);
        }

        @NonNull
        private Option convertToDomainOptionFrom(
                @NonNull MetadataConfigurationsApi.Option apiOption) {

            return Option.newBuilder()
                    .name(apiOption.name)
                    .code(apiOption.code)
                    .attribute(getDefaultAttribute())
                    .build();

        }

        @NonNull
        private Option.Attribute getDefaultAttribute() {
            return Option.Attribute.newBuilder()
                    .id(1)
                    .backgroundColour("#FFFFFF")
                    .horizontalAlignment(Option.Attribute.HorizontalAlignment.NONE)
                    .verticalAlignment(Option.Attribute.VerticalAlignment.NONE)
                    .textSize(20).build();
        }
    }
}
