package org.eyeseetea.malariacare.data.database.convert;

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
    public void convert_a_domain_question_with_one_options_to_questiondb_with_one_question_answer
            () {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionOneOption());

        QuestionDB expectedQuestion = givenADBQuestionWithOneOption();

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_with_no_options_to_questiondb()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWithNonOptions());

        QuestionDB expectedQuestion = givenAQuestionDBNonOptions();

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_dropdown_list_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.DROPDOWN_LIST));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.DROPDOWN_OU_LIST);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_short_text_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.SHORT_TEXT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.SHORT_TEXT);

        assertEqual(questionToEvaluate, expectedQuestion);
    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_phone_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.PHONE));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.PHONE);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_year_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.YEAR));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.YEAR);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_date_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.DATE));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.DATE);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_long_output() throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.LONG_TEXT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.LONG_TEXT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_positive_int_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.POSITIVE_INT));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.POSITIVE_INT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_pregnant_month_int_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.PREGNANT_MONTH));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.PREGNANT_MONTH_INT);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_radio_group_horizontal_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.RADIO_GROUP_HORIZONTAL));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.RADIO_GROUP_HORIZONTAL);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_question_label_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.QUESTION_LABEL));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.QUESTION_LABEL);

        assertEqual(questionToEvaluate, expectedQuestion);

    }

    @Test
    public void convert_a_domain_question_to_questiondb_with_switch_button_output()
            throws Exception {

        QuestionDB questionToEvaluate = converter.visit(givenADomainQuestionWith(
                Question.Type.SWITCH_BUTTON));

        QuestionDB expectedQuestion = givenADBQuestionWithOutput(Constants.SWITCH_BUTTON);

        assertEqual(questionToEvaluate, expectedQuestion);

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


        List<QuestionOptionDB> questionOptionDBSToEvaluate =
                questionToEvaluate.getQuestionOptionDBS();

        List<QuestionOptionDB> questionOptionDBSToExpected =
                expectedQuestion.getQuestionOptionDBS();
        if (questionOptionDBSToEvaluate != null && questionOptionDBSToExpected != null) {
            assertEqual(questionOptionDBSToEvaluate, questionOptionDBSToExpected);
        }
    }

    private void assertEqual(List<QuestionOptionDB> questionOptionDBSToEvaluate,
            List<QuestionOptionDB> questionOptionDBSToExpected) {

        int sizeQuestionOptions = questionOptionDBSToEvaluate.size();
        for (int i = 0; i < sizeQuestionOptions; i++) {

            QuestionOptionDB questionOptionDBToEvaluate = questionOptionDBSToEvaluate.get(i);
            QuestionOptionDB expectedQuestionOptionDB = questionOptionDBSToExpected.get(i);

            assertEqual(questionOptionDBToEvaluate, expectedQuestionOptionDB);
        }
    }

    private void assertEqual(QuestionOptionDB questionOptionDBToEvaluate, QuestionOptionDB
            expectedQuestionOptionDB) {

        OptionDB optionDBToEvaluate = questionOptionDBToEvaluate.getOptionDB();
        OptionDB expectedOptionDB = expectedQuestionOptionDB.getOptionDB();

        assertThat(optionDBToEvaluate.getCode(), is(expectedOptionDB.getCode()));
        assertThat(optionDBToEvaluate.getName(), is(expectedOptionDB.getName()));
    }


    private Question givenADomainQuestionWith(Question.Type type) {
        Question question = givenADomainQuestionWithNonOptions();
        question.setType(type);

        return question;
    }

    private Question givenADomainQuestionOneOption() {
        Question question = givenADomainQuestionWithNonOptions();

        question.setOptions(new ArrayList<Option>(1));

        Option firstOption = new Option();
        firstOption.setCode("FPL");
        firstOption.setName("common_option_program_familyPlanning");

        question.getOptions().add(firstOption);


        return question;
    }

    private QuestionDB givenADBQuestionWithOneOption() {
        QuestionDB questionDB = givenAQuestionDB();
        List<QuestionOptionDB> questionOptionDBS = new ArrayList<>(1);

        OptionDB optionDB = new OptionDB();
        optionDB.setCode("FPL");
        optionDB.setName("common_option_program_familyPlanning");

        QuestionOptionDB questionOptionDB = new QuestionOptionDB();
        questionOptionDB.setOption(optionDB);

        questionOptionDBS.add(questionOptionDB);

        questionDB.setQuestionOptionDBS(questionOptionDBS);

        return questionDB;
    }

    private Question givenADomainQuestionWithNonOptions() {
        Question question = new Question();
        question.setCode("program");
        question.setName("ipc_issueEntry_q_program");
        question.setType(Question.Type.DROPDOWN_LIST);
        question.setVisibility(Question.Visibility.VISIBLE);
        question.setCompulsory(true);


        return question;
    }

    private QuestionDB givenADBQuestionWithOutput(int output) {
        QuestionDB question = givenAQuestionDB();
        question.setOutput(output);
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