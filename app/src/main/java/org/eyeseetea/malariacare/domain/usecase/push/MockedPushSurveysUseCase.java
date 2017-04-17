package org.eyeseetea.malariacare.domain.usecase.push;


import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

import java.util.List;

public class MockedPushSurveysUseCase {
    public void execute(Callback callback) {
        List<Survey> surveys = Survey.getAllMalariaSurveysToBeSent(
                new SurveyFragmentStrategy().getMalariaProgram());

        for (Survey survey : surveys) {
            SurveyFragmentStrategy.setSurveyAsSent(survey);
        }
        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

