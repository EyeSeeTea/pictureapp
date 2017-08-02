package org.eyeseetea.malariacare.domain.usecase.push;


import org.eyeseetea.malariacare.data.database.model.SurveyDB;
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
        List<SurveyDB> surveyDBs = SurveyDB.getAllMalariaSurveysToBeSent(
                mProgramLocalDataSource.getUserProgram().getId());

        for (SurveyDB surveyDB : surveyDBs) {
            SurveyFragmentStrategy.setSurveyAsSent(surveyDB);
        }
        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

