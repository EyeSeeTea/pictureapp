package org.eyeseetea.malariacare.domain.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OverLimitSurveysDomainServiceTest {
    @Test
    public void should_return_false_if_count_threshold_is_equal_to_0() {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(0)
                        .withTimeHour(1)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(false));
    }

    @Test
    public void should_return_false_if_time_hours_threshold_is_equal_to_0() {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(20)
                        .withTimeHour(0)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(false));
    }

    @Test
    public void
    should_return_false_if_surveys_count_is_over_threshold_and_difference_time_is_over_threshold() {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(20)
                        .withTimeHour(1)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(false));
    }

    @Test
    public void
    should_return_false_if_surveys_count_is_under_threshold_and_difference_time_is_over_threshold
            () {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(40)
                        .withTimeHour(1)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(false));
    }

    @Test
    public void
    should_return_true_if_surveys_count_is_equal_to_threshold_and_difference_time_is_equal_to_threshold() {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(30)
                        .withTimeHour(2)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(true));
    }

    @Test
    public void
    should_return_true_if_surveys_count_is_over_threshold_and_difference_time_is_under_threshold() {
        List<Survey> surveys = givenThereAre30SurveysIn2Hours();

        SurveysThresholds surveyThreshold =
                new SurveysThresholdBuilder()
                        .withCount(20)
                        .withTimeHour(3)
                        .build();

        boolean isOverLimit = OverLimitSurveysDomainService.isSurveysOverLimit(surveys,
                surveyThreshold);

        assertThat(isOverLimit, is(true));
    }

    private List<Survey> givenThereAre30SurveysIn2Hours() {
        List<Survey> surveys = new ArrayList<>();

        Date minDate = new Date();
        Date maxDate = new Date(minDate.getTime() + TimeUnit.HOURS.toMillis(2));

        for (int i = 1; i <= 30; i++) {
            Survey survey;

            if (i % 2 == 0) {
                survey = new Survey(minDate);
            } else {
                survey = new Survey(maxDate);
            }

            surveys.add(survey);
        }

        return surveys;
    }
}
