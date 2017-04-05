package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;

public abstract class ASurveyFragmentStrategy {

    abstract Survey getRenderSurvey(Question screenQuestion);

    public boolean isDynamicStockQuestion(String uid) {
        return false;
    }

    abstract boolean isStockSurvey(Survey survey);

    abstract String getMalariaProgram();
}
