package org.eyeseetea.malariacare.domain.usecase.push;


import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

import java.util.List;

public class MockedPushSurveysUseCase {
    private IProgramRepository mProgramLocalDataSource;

    public MockedPushSurveysUseCase(
            IProgramRepository programLocalDataSource) {
        mProgramLocalDataSource = programLocalDataSource;
    }

    public void execute(Callback callback) {
        List<Survey> surveys = Survey.getAllMalariaSurveysToBeSent(
                mProgramLocalDataSource.getUserProgram().getId());

        for (Survey survey : surveys) {
            SurveyFragmentStrategy.setSurveyAsSent(survey);
        }
        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

