package org.eyeseetea.malariacare.data.sync.importer;

import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.MZ_CONFIG_FILE_JSON;
import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.NP_CONFIG_FILE_JSON;
import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.TZ_CONFIG_FILE_JSON;
import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.ZW_CONFIG_FILE_JSON;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.Metadata;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MetadataConfigurationApiClientShould {


    private MetadataConfigurationApiClient apiClient;

    private CustomMockServer CustomMockServer;

    private Metadata metadata;

    private List<Configuration.CountryVersion> countryVersions;

    @Before
    public void setUp() throws Exception {

        CustomMockServer = new CustomMockServer(new FileReader());

        apiClient = new MetadataConfigurationApiClient(CustomMockServer.getBaseEndpoint(),
                new BasicAuthInterceptor(""));
    }

    @After
    public void teardown() throws IOException {
        CustomMockServer.shutdown();
    }

    @Test
    public void parse_mz_configuration_response() throws Exception {

        whenRequestQuestionsForMZCountry();

        thenAssertThatResponseParseSuccessfullyForMZCountry();
    }

    @Test
    public void parse_tz_configuration_response() throws Exception {

        whenRequestQuestionsForTZCountry();

        thenAssertThatResponseParseSuccessfullyForTZCountry();
    }

    @Test
    public void parse_zw_configuration_response() throws Exception {

        whenRequestQuestionsForZWCountry();

        thenAssertThatResponseParseSuccessfullyForZWCountry();
    }

    @Test
    public void parse_np_configuration_response() throws Exception {

        whenRequestQuestionsForNPCountry();

        thenAssertThatResponseParseSuccessfullyForNPCountry();

    }

    @Test
    public void parse_countries_versions_response() throws Exception {

        whenRequestCountryVersions();

        thenAssertThatResponseParseSuccessfullyForCountryVersions();
    }

    @Test(expected = ApiCallException.class)
    public void throw_an_exception_a_network_exception_when_404_http_error_occurs()
            throws Exception {

        whenA404ErrorHappen();

    }


    @Test(expected = ApiCallException.class)
    public void throw_an_exception_when_the_response_is_a_malformed_json() throws Exception {

        whenReceiveAMalformedJSON();
    }


    private void thenAssertThatResponseParseSuccessfullyForMZCountry() {
        Question question = metadata.getQuestions().get(0);
        validateQuestion(question, givenAValidQuestionForMZ());
        validateOptions(metadata.getOptionsByQuestion(question.getCode()), givenAValidOptionsForMZ());
    }

    private void thenAssertThatResponseParseSuccessfullyForNPCountry() {
        Question question = metadata.getQuestions().get(0);
        validateQuestion(question, givenAValidQuestionForNP());
        validateOptions(metadata.getOptionsByQuestion(question.getCode()), givenAValidOptionForNP());
        validateQuestion(metadata.getQuestions().get(0), givenAValidQuestionForNP());
    }

    private void thenAssertThatResponseParseSuccessfullyForTZCountry() {
        Question question = metadata.getQuestions().get(1);
        validateQuestion(question, givenAValidQuestionForTZ());
        validateOptions(metadata.getOptionsByQuestion(question.getCode()), null);
        validateQuestion(metadata.getQuestions().get(1), givenAValidQuestionForTZ());
    }

    private void thenAssertThatResponseParseSuccessfullyForZWCountry() {
        Question question = metadata.getQuestions().get(3);
        validateQuestion(question, givenAValidQuestionForZW());
        validateOptions(metadata.getOptionsByQuestion(question.getCode()), null);
        validateQuestion(metadata.getQuestions().get(3), givenAValidQuestionForZW());
    }

    private void thenAssertThatResponseParseSuccessfullyForCountryVersions() {

        Configuration.CountryVersion countryVersion = countryVersions.get(0);

        assertThat(countryVersion.getCountry(), is("T_TZ"));
        assertThat(countryVersion.getUid(), is("low6qUS2wc9"));
        assertThat(countryVersion.getVersion(), is(1));
        assertThat(countryVersion.getReference(), is("dc@TZ@v1"));
    }

    private void enqueueMalformedJson() throws IOException {
        CustomMockServer.enqueueMockResponse(200, "{malformedJson}");

    }

    private void whenRequestQuestionsForMZCountry() throws Exception {
        requestQuestionsFor(MZ_CONFIG_FILE_JSON, "dc@MZ@v1");
    }

    private void whenRequestQuestionsForNPCountry() throws Exception {
        requestQuestionsFor(NP_CONFIG_FILE_JSON, "dc@NP@v1");
    }

    private void whenRequestQuestionsForTZCountry() throws Exception {
        requestQuestionsFor(TZ_CONFIG_FILE_JSON, "dc@TZ@v1");
    }

    private void whenRequestQuestionsForZWCountry() throws Exception {
        requestQuestionsFor(ZW_CONFIG_FILE_JSON, "dc@ZW@v1");
    }

    private void whenRequestCountryVersions() throws Exception {

        CustomMockServer.enqueueMockResponse(COUNTRIES_VERSION);

        countryVersions = apiClient.getCountriesVersions();

    }

    private void requestQuestionsFor(String countryFile, String countryCode) throws Exception {

        CustomMockServer.enqueueMockResponse(countryFile);

        metadata = apiClient.getQuestionsByCountryCode(countryCode);

    }

    private void validateQuestion(Question questionToValidate,
            Question expectedQuestion) {

        assertThat(questionToValidate.getCode(), is(expectedQuestion.getCode()));

        assertThat(questionToValidate.getName(), is(expectedQuestion.getName()));

        assertThat(questionToValidate.getType(), is(expectedQuestion.getType()));

        assertThat(questionToValidate.isCompulsory(), is(expectedQuestion.isCompulsory()));
    }

    private void validateOptions(List<Option> optionsToValidate, List<Option> expectedOptions){
        if (expectedOptions != null) {
            assertThat(optionsToValidate, is(notNullValue()));

            for (int i = 0; i < expectedOptions.size(); i++) {

                Option validOption = expectedOptions.get(i);
                Option toValidateOption = optionsToValidate.get(i);

                assertThat(toValidateOption.getCode(), is(validOption.getCode()));
                assertThat(toValidateOption.getName(), is(validOption.getName()));
            }
        }
    }

    private Question givenAValidQuestionForMZ() {

        Question mzQuestion = Question.
                newBuilder()
                .uid("uid")
                .code("program")
                .name("ipc_issueEntry_q_program")
                .type(Question.Type.DROPDOWN_LIST)
                .compulsory(true)
                .build();

        return mzQuestion;
    }

    private List<Option> givenAValidOptionsForMZ() {
        Option firstOption = Option
                .newBuilder()
                .code("FPL")
                .name("common_option_program_familyPlanning")
                .build();

        return createListOption(firstOption);
    }

    private Question givenAValidQuestionForNP() {

        Question mzQuestion = Question
                .newBuilder()
                .uid("uid")
                .code("program")
                .name("ipc_issueEntry_q_program")
                .type(Question.Type.DROPDOWN_LIST)
                .compulsory(true)
                .visibility(Question.Visibility.VISIBLE)
                .build();

        return mzQuestion;
    }

    private List<Option> givenAValidOptionForNP() {
        Option firstOption = Option
                .newBuilder()
                .code("FPL")
                .name("common_option_program_familyPlanning")
                .build();

        return createListOption(firstOption);
    }

    private Question givenAValidQuestionForTZ() {

        return Question
                .newBuilder()
                .uid("uid")
                .code("firstName")
                .name("ipc_issueEntry_q_firstName")
                .type(Question.Type.SHORT_TEXT)
                .compulsory(true)
                .build();
    }

    private Question givenAValidQuestionForZW() {

       return Question
                .newBuilder()
                .uid("uid")
                .code("age")
                .name("ipc_issueEntry_q_age")
                .type(Question.Type.INT)
                .compulsory(true)
                .regExp("^(\\d{2})$")
                .regExpError("some_error_msg_ref")
                .build();
    }

    private void whenA404ErrorHappen() throws Exception {
        CustomMockServer.enqueueMockResponse(404);
        apiClient.getQuestionsByCountryCode("mz");
    }

    private void whenReceiveAMalformedJSON() throws Exception {
        enqueueMalformedJson();

        apiClient.getQuestionsByCountryCode("dc@MZ@v1");
    }

    private List<Option> createListOption(Option option)
    {
        List<Option> options = new ArrayList<>();
        options.add(option);
        return options;
    }
}
