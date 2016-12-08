package org.eyeseetea.malariacare.domain.usecase;


import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.database.model.Survey;

import java.util.List;

public class MockedPushSurveysUseCase {
    public void execute(Callback callback) {
        List<Survey> surveys = Survey.getAllSurveysToBeSent();

        //Check surveys not in progress
        for (Survey survey : surveys) {
            survey.setStatus(SURVEY_SENT);
            survey.save();
        }

        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

