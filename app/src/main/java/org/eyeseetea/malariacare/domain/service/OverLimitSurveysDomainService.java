package org.eyeseetea.malariacare.domain.service;

import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OverLimitSurveysDomainService {

    public static boolean isSurveysOverLimit(List<Survey> surveys,
            SurveysThresholds surveysThresholds) {

        if (surveysThresholds.getCount() > 0 &&
                surveysThresholds.getTimeHours() > 0 &&
                surveys.size() >= surveysThresholds.getCount()) {

            sortSurveysByDate(surveys);

            long firstSurveyDate = surveys.get(0).getSurveyDate().getTime();
            long lastSurveyDate = surveys.get(surveys.size() - 1).getSurveyDate().getTime();

            long timeDifference = TimeUnit.MILLISECONDS.toHours(lastSurveyDate - firstSurveyDate);

            return (timeDifference <= surveysThresholds.getTimeHours());
        }
        return false;
    }

    private static void sortSurveysByDate(List<Survey> surveys) {
        Collections.sort(surveys, new Comparator<Survey>() {
            @Override
            public int compare(Survey survey1, Survey survey2) {
                return survey1.getSurveyDate().compareTo(survey2.getSurveyDate());
            }
        });
    }
}
