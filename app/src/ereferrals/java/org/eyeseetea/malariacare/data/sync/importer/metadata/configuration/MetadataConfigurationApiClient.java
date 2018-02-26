package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.ACTION_SHOW;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_INT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_LONG_TEXT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_POSITIVE_INT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_PREGNANT_MONTH_INT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_QUESTION_LABEL;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_RADIO_GROUP_HORIZONTAL;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_SWITCH_BUTTON;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_TYPE_DATE;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_TYPE_DROPDOWN_LIST;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_TYPE_PHONE;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_TYPE_SHORT_TEXT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.CONTROL_TYPE_YEAR;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.DISPLAY_PRIORITY_IMPORTANT;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.DISPLAY_PRIORITY_INVISIBLE;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.DISPLAY_PRIORITY_VISIBLE;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.OPERATOR_EQUAL;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.OPERATOR_GREATER_OR_EQUAL_THAN;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.OPERATOR_GREATER_THAN;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.OPERATOR_LESS_OR_EQUAL_THAN;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.OPERATOR_LESS_THAN;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.TYPE_DATA_POINT_REF;
import static org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi.Question.TYPE_VALUE;
import static org.eyeseetea.malariacare.domain.entity.Question.Visibility.IMPORTANT;
import static org.eyeseetea.malariacare.domain.entity.Question.Visibility.INVISIBLE;
import static org.eyeseetea.malariacare.domain.entity.Question.Visibility.VISIBLE;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.converter.PhoneFormatConvertToDomainVisitor;
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

        return newOptionRule;
    }

    private void assignRuleTo(
            @NonNull MetadataConfigurationsApi.Rule apiRule,
            @NonNull Map<String, MetadataConfigurationsApi.Question> questionsByCode) {

        List<MetadataConfigurationsApi.Condition> conditions = apiRule.conditions;

        for (int i = 0; i < conditions.size(); i++) {
            MetadataConfigurationsApi.Condition condition = conditions.get(i);

            String questionCodeWithRule = condition.left.value;

            MetadataConfigurationsApi.Question questionWithRule = questionsByCode.get(
                    questionCodeWithRule);

            if (questionWithRule.output.equals(CONTROL_TYPE_DROPDOWN_LIST)) {

                assignRulesToQuestionOptions(apiRule, questionsByCode, condition, questionWithRule);

            } else {

                assignRulesToQuestions(apiRule, questionWithRule);
            }
        }
    }

    private void assignRulesToQuestions(@NonNull MetadataConfigurationsApi.Rule apiRule,
            MetadataConfigurationsApi.Question questionWithRule) {
        if (questionWithRule.rules == null) {
            questionWithRule.rules = new ArrayList<>();
        }

        questionWithRule.rules.add(apiRule);
    }

    private void assignRulesToQuestionOptions(@NonNull MetadataConfigurationsApi.Rule apiRule,
            @NonNull Map<String, MetadataConfigurationsApi.Question> questionsByCode,
            MetadataConfigurationsApi.Condition condition,
            MetadataConfigurationsApi.Question questionWithRule) {

        String optionCodeWithRule = condition.right.value;

        MetadataConfigurationsApi.Option optionWithoutRule = findOptionBy(
                optionCodeWithRule,
                questionWithRule);


        for (MetadataConfigurationsApi.Action action : apiRule.actions) {

            String targetQuestionCode = action.dataPointRef;

            if (optionWithoutRule != null) {

                MetadataConfigurationsApi.Question targetQuestion = questionsByCode
                        .get(targetQuestionCode);

                if (targetQuestion != null) {
                    assignRuleTo(optionWithoutRule, condition, targetQuestion);
                }
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

        for (MetadataConfigurationsApi.Question question : questions) {
            map.put(question.code, question);
        }

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
        private final PhoneFormatConvertToDomainVisitor phoneFormatConverter =
                new PhoneFormatConvertToDomainVisitor();

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

                Question domainQuestion = convertToDomainQuestionFrom(apiQuestion, questionIndex);

                domainQuestions.add(domainQuestion);
                mapDomainQuestionsByCode.put(domainQuestion.getCode(), domainQuestion);

                if(!isImportantQuestionSelected) {
                    isImportantQuestionSelected = isImportantQuestion(domainQuestion);
                }
            }

            setImportantDomainQuestion(domainQuestions, isImportantQuestionSelected);

            assignRulesToQuestions();

            return domainQuestions;
        }

        private void setImportantDomainQuestion(List<Question> domainQuestions,
                boolean isImportantQuestionSelected) {

            if(!isImportantQuestionSelected && domainQuestions.size() >=1){
                Question domainQuestion  = domainQuestions.get(0);
                domainQuestion.setVisibility(IMPORTANT);
            }
        }


        private boolean isImportantQuestion(Question question) {
            return question.getVisibility() == Question.Visibility.IMPORTANT;
        }

        private List<Question.Rule> convertToDomainRules(
                List<MetadataConfigurationsApi.Rule> apiRules) {

            if (apiRules == null) return null;

            List<Question.Rule> domainRules = new ArrayList<>();

            for (MetadataConfigurationsApi.Rule apiRule : apiRules) {
                domainRules.add(newDomainRule(apiRule));
            }
            return domainRules;
        }

        private Question.Rule newDomainRule(MetadataConfigurationsApi.Rule apiRule) {
            List<Question.Rule.Condition> domainConditions = new ArrayList<>();
            List<Question.Rule.Action> domainActions = new ArrayList<>();


            for (MetadataConfigurationsApi.Condition apiCondition : apiRule.conditions) {

                Question.Rule.Condition domainCondition = convertToDomainCondition(apiCondition);

                domainConditions.add(domainCondition);
            }

            for (MetadataConfigurationsApi.Action apiAction : apiRule.actions) {

                domainActions.add(convertToDomainAction(apiAction));
            }

            return Question.Rule.newBuilder()
                    .conditions(domainConditions)
                    .actions(domainActions)
                    .build();
        }

        private Question.Rule.Action convertToDomainAction(
                MetadataConfigurationsApi.Action apiAction) {
            Question.Rule.ActionToPerform domainActionToPerform = null;

            switch (apiAction.action) {
                case ACTION_SHOW: {
                    domainActionToPerform = Question.Rule.ActionToPerform.SHOW;
                }
            }

            return Question.Rule.Action.newBuilder()
                    .targetQuestion(apiAction.dataPointRef)
                    .actionToPerform(domainActionToPerform)
                    .build();
        }

        @NonNull
        private Question.Rule.Condition convertToDomainCondition(
                MetadataConfigurationsApi.Condition apiCondition) {
            return Question.Rule.Condition.newBuilder()
                    .left(newDomainOperand(apiCondition.left))
                    .right(newDomainOperand(apiCondition.right))
                    .operator(newDomainOperator(apiCondition.operator))
                    .build();
        }

        private Question.Rule.Operator newDomainOperator(String apiOperator) {
            Question.Rule.Operator domainOperator = null;

            switch (apiOperator) {
                case OPERATOR_EQUAL: {
                    domainOperator = Question.Rule.Operator.EQUAL;
                    break;
                }
                case OPERATOR_GREATER_THAN: {
                    domainOperator = Question.Rule.Operator.GREATER_THAN;
                    break;
                }
                case OPERATOR_GREATER_OR_EQUAL_THAN: {
                    domainOperator = Question.Rule.Operator.GREATER_OR_EQUAL_THAN;
                    break;
                }
                case OPERATOR_LESS_THAN: {
                    domainOperator = Question.Rule.Operator.LESS_THAN;
                    break;
                }

                case OPERATOR_LESS_OR_EQUAL_THAN: {
                    domainOperator = Question.Rule.Operator.LESS_OR_EQUAL_THAN;
                    break;
                }


            }
            return domainOperator;
        }

        private Question.Rule.Operand newDomainOperand(
                MetadataConfigurationsApi.Operand apiOperand) {

            return Question.Rule.Operand
                    .newBuilder()
                    .value(apiOperand.value)
                    .type(convertToDomainOperandType(apiOperand.type))
                    .build();
        }

        private Question.Rule.OperandType convertToDomainOperandType(String apiOperand) {
            Question.Rule.OperandType operandType = null;
            switch (apiOperand) {
                case TYPE_DATA_POINT_REF: {
                    operandType = Question.Rule.OperandType.QUESTION;
                    break;
                }
                case TYPE_VALUE: {
                    operandType = Question.Rule.OperandType.VALUE;
                    break;
                }
            }
            return operandType;
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
                @NonNull MetadataConfigurationsApi.Question apiQuestion, int index) {

            return Question.newBuilder()
                    .code(apiQuestion.code)
                    .uid(apiQuestion.code)
                    .phoneFormat(convertToDomainPhoneFormatFrom(apiQuestion.phoneFormat))
                    .name(apiQuestion.deName)
                    .index(index)
                    .type(convertToDomainQuestionTypeFrom(apiQuestion.output))
                    .visibility(getVisibilityFrom(apiQuestion))
                    .options(convertToDomainOptionsFrom(apiQuestion.options, apiQuestion))
                    .compulsory(apiQuestion.compulsory)
                    .rules(convertToDomainRules(apiQuestion.rules))
                    .build();
        }

        @NonNull
        private Question.Visibility getVisibilityFrom(
                @NonNull MetadataConfigurationsApi.Question
                        apiQuestion) {

            Question.Visibility domainVisibility;

            switch (apiQuestion.queueDisplayPriority){

                case DISPLAY_PRIORITY_VISIBLE:{
                    domainVisibility = VISIBLE;
                    break;
                }

                case DISPLAY_PRIORITY_INVISIBLE:{
                    domainVisibility = INVISIBLE;
                    break;
                }

                case DISPLAY_PRIORITY_IMPORTANT:{
                    domainVisibility = IMPORTANT;
                    break;
                }
                default:
                    domainVisibility = VISIBLE;
            }

            return domainVisibility;
        }

        @Nullable
        private PhoneFormat convertToDomainPhoneFormatFrom(
                MetadataConfigurationsApi.PhoneFormat apiPhoneFormat) {

            if (apiPhoneFormat != null) {
                return phoneFormatConverter.visit(apiPhoneFormat);
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
