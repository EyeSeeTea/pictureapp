package org.eyeseetea.malariacare.data.database.convert;

import static junit.framework.TestCase.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.mappers.QuestionConvertFromDomainVisitor;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QuestionConvertFromDomainVisitorShould {

    private QuestionConvertFromDomainVisitor converter;

    @Before
    public void setUp() throws Exception {
        converter = new QuestionConvertFromDomainVisitor(
                ConverterFactory.getOptionConverter(), ConverterFactory.getPhoneFormatConverter());
    }

    @Test
    public void convert_a_domain_question_with_suffix() {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionOneOptionAndSuffix());

        QuestionDB expectedQuestion = givenADBQuestionWithOneOption();

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_with_one_options_to_questiondb_with_one_question_answer
            () {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionOneOption());

        QuestionDB expectedQuestion = givenADBQuestionWithOneOption();

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_with_no_options_to_questiondb()
            throws Exception {

        Question question = givenADomainQuestionBuilderWithNonOptions();

        QuestionDB questionToEvaluate = converter.visit(question);

        QuestionDB expectedQuestion = givenAQuestionDBNonOptions();

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_empty_default_value()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithDefaultValue(
                ""));

        QuestionDB expectedQuestion = givenADBQuestionWithDefaultValue("");

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_default_value()
            throws Exception {

        String defaultValue = "defaultValue";
        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithDefaultValue(
                defaultValue));

        QuestionDB expectedQuestion = givenADBQuestionWithDefaultValue(defaultValue);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_dropdown_list_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.DROPDOWN_LIST));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.DROPDOWN_OU_LIST);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_regexp_and_regexp_error()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithRegex(
                "^(\\d{2})$", "some_error_msg_ref\""));

        QuestionDB expectedQuestion = givenADBQuestionWithValidation("^(\\d{2})$",
                "some_error_msg_ref\"");

        assertEqual(questionToEvaluate, expectedQuestion);
    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_short_text_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.SHORT_TEXT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.SHORT_TEXT);

        assertEqual(questionToEvaluate, expectedQuestion);
    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_phone_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.PHONE));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.PHONE);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_year_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.YEAR));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.YEAR);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_date_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.DATE));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.DATE);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_long_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.LONG_TEXT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.LONG_TEXT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_positive_int_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.POSITIVE_INT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.POSITIVE_INT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_pregnant_month_int_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.PREGNANT_MONTH));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.PREGNANT_MONTH_INT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_radio_group_horizontal_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.RADIO_GROUP_HORIZONTAL));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.RADIO_GROUP_HORIZONTAL);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_question_label_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.QUESTION_LABEL));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.QUESTION_LABEL);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_switch_button_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithType(
                Question.Type.SWITCH_BUTTON));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.SWITCH_BUTTON);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_with_visibility_important() {
        Question domainImportantQuestion = givenADomainQuestionWithVisibility(
                Question.Visibility.IMPORTANT);

        QuestionDB dbImportantQuestion = converter.visit(domainImportantQuestion);

        assertTrue(dbImportantQuestion.isImportant());
    }

    @Test
    public void convert_a_domain_question_with_visibility_invisible() {
        Question domainImportantQuestion = givenADomainQuestionWithVisibility(
                Question.Visibility.INVISIBLE);

        QuestionDB dbImportantQuestion = converter.visit(domainImportantQuestion);

        assertTrue(dbImportantQuestion.isInvisible());
    }

    private void assertEqualQuestionsProperties(QuestionDB questionToEvaluate,
            QuestionDB expectedQuestion) {
        assertThat(questionToEvaluate.getCode(), is(expectedQuestion.getCode()));
        assertThat(questionToEvaluate.getDe_name(), is(expectedQuestion.getDe_name()));
        assertThat(questionToEvaluate.getForm_name(), is(expectedQuestion.getForm_name()));
        assertThat(questionToEvaluate.getOutput(), is(expectedQuestion.getOutput()));
        assertThat(questionToEvaluate.isCompulsory(), is(expectedQuestion.isCompulsory()));

    }

    private void assertEqual(QuestionDB questionToEvaluate, QuestionDB expectedQuestion) {

        assertEqualQuestionsProperties(questionToEvaluate, expectedQuestion);

    }

    private Question givenADomainQuestionWithType(Question.Type type) {
        return givenADomainQuestion(null, null, type, null, null, null);
    }

    private Question givenADomainQuestionWithDefaultValue(String defaultValue) {
        return givenADomainQuestion(null, null, Question.Type.DROPDOWN_LIST, defaultValue, null,
                null);
    }

    private Question givenADomainQuestionWithRegex(String regexp, String regExpError) {
        return givenADomainQuestion(null, null, Question.Type.DROPDOWN_LIST,
                null, regexp, regExpError);
    }

    private Question givenADomainQuestionOneOptionAndSuffix() {
        List<Option> options = new ArrayList<>(1);

        Option firstOption = Option.newBuilder()
                .code("YES")
                .name("ipc_issueEntry_q_blank")
                .build();

        options.add(firstOption);

        Question.VoucherCodeSuffix suffix =
                new Question.VoucherCodeSuffix("MAC", "YES");

        Question question = givenADomainQuestion(options, suffix, Question.Type.DROPDOWN_LIST,
                null, null, null);

        return question;
    }

    private Question givenADomainQuestionWithVisibility(Question.Visibility visibility) {
        Question question = givenADomainQuestionBuilderWithNonOptions();
        question.setVisibility(visibility);
        return question;
    }

    private Question givenADomainQuestionOneOption() {
        List<Option> options = new ArrayList<>(1);

        Option firstOption = Option.newBuilder()
                .code("FPL")
                .name("common_option_program_familyPlanning")
                .build();

        options.add(firstOption);

        return givenADomainQuestion(options, null, Question.Type.DROPDOWN_LIST, null, null,
                null);
    }

    private Question givenADomainQuestionBuilderWithNonOptions() {
        return givenADomainQuestion(null, null, Question.Type.DROPDOWN_LIST, null, null,
                null);
    }

    private Question givenADomainQuestion(List<Option> options, Question.VoucherCodeSuffix suffix,
            Question.Type type, String defaultValue, String regexp, String regExpError) {

        Question question = Question
                .newBuilder()
                .uid("uid")
                .code("program")
                .name("ipc_issueEntry_q_program")
                .type(type)
                .visibility(Question.Visibility.VISIBLE)
                .options(options)
                .compulsory(true)
                .defaultValue(defaultValue)
                .regExp(regexp)
                .regExpError(regExpError)
                .voucherCodeSuffix(suffix)
                .build();

        return question;
    }

    private QuestionDB givenADBQuestionWithOneOption() {
        QuestionDB questionDB = givenAQuestionDB();

        OptionDB optionDB = new OptionDB();
        optionDB.setCode("FPL");
        optionDB.setName("common_option_program_familyPlanning");

        QuestionOptionDB questionOptionDB = new QuestionOptionDB();
        questionOptionDB.setOption(optionDB);

        return questionDB;
    }

    private QuestionDB givenADBQuestionWithOneOptionAndSuffix() {
        QuestionDB questionDB = givenAQuestionDB();

        OptionDB optionDB = new OptionDB();

        optionDB.setCode("YES");
        optionDB.setName("ipc_issueEntry_q_blank");

        QuestionOptionDB questionOptionDB = new QuestionOptionDB();
        questionOptionDB.setOption(optionDB);

        questionDB.setVoucher_suffix("MAC");
        questionDB.setVoucher_suffix_value_condition("YES");

        return questionDB;
    }


    private QuestionDB givenADBQuestionWithOutput(int output) {
        QuestionDB question = givenAQuestionDB();
        question.setOutput(output);
        return question;
    }

    private QuestionDB givenADBQuestionWithDefaultValue(String defaultValue) {
        QuestionDB question = givenAQuestionDB();
        question.setDefaultValue(defaultValue);
        return question;
    }

    private QuestionDB givenADBQuestionWithValidation(String validation, String validationMessage) {
        QuestionDB question = givenAQuestionDB();
        question.setValidationRegExp(validation);
        question.setValidationMessage(validationMessage);
        return question;
    }

    private QuestionDB givenAQuestionDB() {
        QuestionDB question = new QuestionDB();

        question.setCode("program");
        question.setForm_name("ipc_issueEntry_q_program");
        question.setDe_name("ipc_issueEntry_q_program");
        question.setOutput(Constants.DROPDOWN_OU_LIST);
        question.setCompulsory(1);

        return question;
    }

    private QuestionDB givenAQuestionDBNonOptions() {
        return givenAQuestionDB();
    }

}