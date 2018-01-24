package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.MetadataApiConfigurations;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.network.factory.HTTPClientFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MetadataConfigurationApiClient implements IMetadataConfigurationDataSource {

    private IMetadataApiConfiguration configurationApi;

    public MetadataConfigurationApiClient(String url) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(HTTPClientFactory.getHTTPClientWithLogging())
                .baseUrl(url)
                .build();

        configurationApi = retrofit.create(IMetadataApiConfiguration.class);
    }

    @Override
    public List<Question> getQuestions() throws Exception {

        MetadataConfigurationConverterApiModelToDomain
                converter = new MetadataConfigurationConverterApiModelToDomain();
        List<MetadataApiConfigurations.Question> apiQuestions = new ArrayList<>();

        try {

            Response<MetadataApiConfigurations> response =
                    configurationApi.getConfiguration().execute();

            MetadataApiConfigurations metadata = getResultsOrThrowException(response);


            if (isApiQuestionNotNull(metadata)) {
                apiQuestions = response.body().issuingCapture.questions;
            }
        } catch (Exception e) {
            throw new ApiCallException(e);
        }

        return converter.convertToDomainQuestionsFrom(apiQuestions);
    }

    private boolean isApiQuestionNotNull(MetadataApiConfigurations metadata) {
        return metadata.issuingCapture != null &&
                metadata.issuingCapture.questions != null;
    }

    @NonNull
    private MetadataApiConfigurations getResultsOrThrowException(
            Response<MetadataApiConfigurations> response)
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
        private final String CONTROL_TYPE_PHONE = "PHONE";
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

        @NonNull
        private List<Question> convertToDomainQuestionsFrom(
                @NonNull List<MetadataApiConfigurations.Question> apiQuestions) {

            List<Question> domainQuestions = new ArrayList<>();

            for (MetadataApiConfigurations.Question apiQuestion : apiQuestions) {

                Question domainQuestion = convertToDomainQuestionFrom(apiQuestion);

                domainQuestion.setOptions(convertToDomainOptionsFrom(apiQuestion.options));

                domainQuestions.add(domainQuestion);

            }
            return domainQuestions;
        }


        @NonNull
        private Question convertToDomainQuestionFrom(
                @NonNull MetadataApiConfigurations.Question apiQuestion) {
            Question question = new Question();

            question.setCode(apiQuestion.code);
            question.setName(apiQuestion.deName);
            question.setType(convertToDomainQuestionTypeFrom(apiQuestion.output));
            question.setCompulsory(apiQuestion.compulsory);


            return question;
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
                @NonNull List<MetadataApiConfigurations.Option> apiOptions) {

            List<Option> domainOptions = new ArrayList<>();

            if (apiOptions == null) {
                return domainOptions;
            }

            for (MetadataApiConfigurations.Option apiOption : apiOptions) {
                domainOptions.add(convertToDomainOptionFrom(apiOption));
            }

            return domainOptions;
        }

        @NonNull
        private Option convertToDomainOptionFrom(
                @NonNull MetadataApiConfigurations.Option apiOption) {
            Option option = new Option();
            option.setName(apiOption.name);
            option.setCode(apiOption.code);
            return option;
        }
    }
}
