package org.eyeseetea.malariacare.domain.usecase;


import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.List;

public class MockedPushSurveysUseCase {
    public void execute(Callback callback) {
    }

    public interface Callback {
        void onPushFinished();
    }
}

