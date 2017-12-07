package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.configurationImporter.BaseMetadataConfigurationImporterTest;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MetadataConfigurationApiClientShould extends BaseMetadataConfigurationImporterTest {

    @Test
    public void parse_mz_configuration_response() {
        final int FIRST_QUESTION_INDEX = 0;

        assertConfigurationResponseParseSuccessfully(FIRST_QUESTION_INDEX,
                MZ_CONFIG_ANDROID_1_0_JSON,
                givenAValidQuestionForMZ());

    }

    @Test(expected = ApiCallException.class)
    public void throw_an_exception_a_network_exception_when_404_http_error_occurs()
            throws Exception {
        enqueue404ResponseCode();
        List<Question> questions = apiClient.getQuestionsFor("mz");

    }

    @Test(expected = ApiCallException.class)
    public void throw_an_exception_when_the_response_is_a_malformed_json() throws Exception {
        enqueueMalformedJson();

        List<Question> questions = apiClient.getQuestionsFor("mz");
    }

    @Test
    public void parse_np_configuration_response() {
        final int FIRST_QUESTION_INDEX = 0;

        assertConfigurationResponseParseSuccessfully(FIRST_QUESTION_INDEX,
                NP_CONFIG_ANDROID_1_0_JSON,
                givenAValidQuestionForNP());

    }

    @Test
    public void parse_tz_configuration_response() {
        final int SECOND_QUESTION_INDEX = 1;
        assertConfigurationResponseParseSuccessfully(SECOND_QUESTION_INDEX,
                TZ_CONFIG_ANDROID_1_0_JSON,
                givenAValidQuestionForTZ());

    }

    @Test
    public void parse_zw_configuration_response() {
        final int FOURTH_QUESTION_INDEX = 3;
        assertConfigurationResponseParseSuccessfully(FOURTH_QUESTION_INDEX,
                ZW_CONFIG_ANDROID_1_0_JSON,
                givenAValidQuestionForZW());

    }

    private void assertConfigurationResponseParseSuccessfully(int questionIndex,
            String countryJSONFile,
            Question validQuestion) {

        Question question = getQuestionsClient(countryJSONFile,"mz").get(questionIndex);

        validateQuestion(question, validQuestion);

    }

    private void validateQuestion(Question questionToValidate,
            Question expectedQuestion) {


        assertThat(questionToValidate.getCode(), is(expectedQuestion.getCode()));

        assertThat(questionToValidate.getName(), is(expectedQuestion.getName()));

        assertThat(questionToValidate.getType(), is(expectedQuestion.getType()));

        assertThat(questionToValidate.isCompulsory(), is(expectedQuestion.isCompulsory()));


        if (expectedQuestion.getOptions() != null) {
            assertThat(questionToValidate.getOptions(), is(notNullValue()));

            for (int i = 0; i < expectedQuestion.getOptions().size(); i++) {

                Option validOption = expectedQuestion.getOptions().get(i);
                Option toValidateOption = questionToValidate.getOptions().get(i);

                assertThat(toValidateOption.getCode(), is(validOption.getCode()));
                assertThat(toValidateOption.getName(), is(validOption.getName()));

            }


        }

    }

    private Question givenAValidQuestionForMZ() {

        Question mzQuestion = new Question();
        mzQuestion.setCode("program");
        mzQuestion.setName("ipc_issueEntry_q_program");
        mzQuestion.setType(Question.Type.DROPDOWN_LIST);
        mzQuestion.setCompulsory(true);
        mzQuestion.setOptions(new ArrayList<Option>(1));

        Option firstOption = new Option();
        firstOption.setCode("FPL");
        firstOption.setName("common_option_program_familyPlanning");

        mzQuestion.getOptions().add(firstOption);

        return mzQuestion;
    }

    private Question givenAValidQuestionForNP() {

        Question mzQuestion = new Question();
        mzQuestion.setCode("program");
        mzQuestion.setName("ipc_issueEntry_q_program");
        mzQuestion.setType(Question.Type.DROPDOWN_LIST);
        mzQuestion.setCompulsory(true);
        mzQuestion.setOptions(new ArrayList<Option>(1));

        Option firstOption = new Option();
        firstOption.setCode("FPL");
        firstOption.setName("common_option_program_familyPlanning");

        mzQuestion.getOptions().add(firstOption);

        return mzQuestion;
    }

    private Question givenAValidQuestionForTZ() {

        Question tzQuestion = new Question();
        tzQuestion.setCode("firstName");
        tzQuestion.setName("ipc_issueEntry_q_firstName");
        tzQuestion.setType(Question.Type.SHORT_TEXT);
        tzQuestion.setCompulsory(true);
        tzQuestion.setOptions(null);

        return tzQuestion;
    }

    private Question givenAValidQuestionForZW() {

        Question zwQuestion = new Question();
        zwQuestion.setCode("age");
        zwQuestion.setName("ipc_issueEntry_q_age");
        zwQuestion.setType(Question.Type.INT);
        zwQuestion.setCompulsory(true);
        zwQuestion.setOptions(null);

        return zwQuestion;
    }
}
