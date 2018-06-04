package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;

public abstract class ASurveyFragmentStrategy {

    public interface Callback {
        void loadIsSurveyCreatedInOtherApp(boolean value);
    }

    abstract SurveyDB getRenderSurvey(QuestionDB screenQuestionDB);

    public boolean isDynamicStockQuestion(String uid) {
        return false;
    }

    abstract boolean isStockSurvey(SurveyDB surveyDB);

    abstract String getMalariaProgram();
}
