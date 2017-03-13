package org.eyeseetea.malariacare.domain.usecase;


import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.List;

public class MockedPushSurveysUseCase {
    public void execute(Callback callback) {
        List<Survey> surveys = Survey.getAllMalariaSurveysToBeSent();

        //Check surveys not in progress
        for (Survey survey : surveys) {
            survey.setStatus(SURVEY_SENT);
            survey.save();
            Survey stockSurvey = Survey.getStockSurveyWithEventDate(survey.getEventDate());
            if (stockSurvey != null) {
                stockSurvey.setStatus(SURVEY_SENT);
                stockSurvey.save();
            }
        }
        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

