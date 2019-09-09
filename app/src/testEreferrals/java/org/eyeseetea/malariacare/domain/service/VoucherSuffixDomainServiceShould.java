package org.eyeseetea.malariacare.domain.service;

import static android.net.sip.SipErrorCode.IN_PROGRESS;

import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class VoucherSuffixDomainServiceShould {

    @Test
    public void return_suffix_if_value_condition_is_equal() {
        List<Question> questions = givenAQuestionsWithSuffix(Arrays.asList("", "MAC", "STR"));

        Survey survey = givenASurveyForQuestions(questions, "YES");

        VoucherSuffixDomainService voucherSuffixDomainService = new VoucherSuffixDomainService();

        String suffix = voucherSuffixDomainService.calculate(survey, questions);

        Assert.assertEquals("_MAC_STR", suffix);
    }

    @Test
    public void return_empty_suffix_if_value_condition_is_not_equal() {
        List<Question> questions = givenAQuestionsWithSuffix(Arrays.asList("", "MAC", "STR"));

        Survey survey = givenASurveyForQuestions(questions, "NO");

        VoucherSuffixDomainService voucherSuffixDomainService = new VoucherSuffixDomainService();

        String suffix = voucherSuffixDomainService.calculate(survey, questions);

        Assert.assertEquals("", suffix);
    }

    @Test
    public void return_empty_suffix_if_does_not_exists_questions_with_suffix() {
        List<Question> questions = givenAQuestionsWithSuffix(Arrays.asList("", "", ""));

        Survey survey = givenASurveyForQuestions(questions,"YES");

        VoucherSuffixDomainService voucherSuffixDomainService = new VoucherSuffixDomainService();

        String suffix = voucherSuffixDomainService.calculate(survey, questions);

        Assert.assertEquals("", suffix);
    }

    private List<Question> givenAQuestionsWithSuffix(List<String> suffixesForQuestions) {
        List<Question> questions = new ArrayList<>();

        int count = 0;

        for (String suffix : suffixesForQuestions) {
            if (suffix.isEmpty()) {
                questions.add(Question.newBuilder()
                        .code("Code")
                        .id(count)
                        .uid(String.valueOf(count))
                        .name(String.valueOf(count))
                        .type(Question.Type.SHORT_TEXT)
                        .build());
            } else {
                questions.add(Question.newBuilder()
                        .code("Code")
                        .id(count)
                        .uid(String.valueOf(count))
                        .name(String.valueOf(count))
                        .type(Question.Type.RADIO_GROUP_HORIZONTAL)
                        .voucherCodeSuffix(new Question.VoucherCodeSuffix(suffix, "YES"))
                        .build());
            }

            count++;
        }

        return questions;
    }


    private Survey givenASurveyForQuestions(List<Question> questions, String valueForSuffixQuestions) {
        long id = 1;
        String uid = "Uid";
        String voucherUid = "voucherUid";
        int status = IN_PROGRESS;
        SurveyAnsweredRatio surveyAnsweredRatio = null;
        Date surveyDate = new Date();
        String programUid = "programUid";
        String orgUnitUid = "orgUnitUid";
        String userUid = "userUid";
        int type = 1;
        List<Value> values = new ArrayList<>();
        String visibleVoucherUid = null;

        for (Question question : questions) {
            Value value;
            if (question.getVoucherCodeSuffix() != null) {
                value = new Value(valueForSuffixQuestions, question.getUid());
            } else {
                value = new Value("text", question.getUid());
            }

            values.add(value);
        }

        Survey survey = new Survey(id, uid, voucherUid, status, surveyAnsweredRatio, surveyDate,
                programUid, orgUnitUid, userUid, type, values, visibleVoucherUid);

        return survey;
    }
}
