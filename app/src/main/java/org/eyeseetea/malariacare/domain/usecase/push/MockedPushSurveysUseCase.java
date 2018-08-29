package org.eyeseetea.malariacare.domain.usecase.push;


import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

import java.util.List;

public class MockedPushSurveysUseCase {
    private IProgramRepository mProgramRepository;

    public MockedPushSurveysUseCase(
            IProgramRepository programRepository) {
        mProgramRepository = programRepository;
    }

    public void execute(Callback callback) {
        List<SurveyDB> surveyDBs = SurveyDB.getAllMalariaSurveysToBeSent(
                mProgramRepository.getUserProgram().getId());

        for (SurveyDB surveyDB : surveyDBs) {
            SurveyFragmentStrategy.setSurveyAsSent(surveyDB);
        }
        callback.onPushFinished();
    }

    public interface Callback {
        void onPushFinished();
    }
}

