package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;

import java.util.Date;

public abstract class ASurveyFragmentStrategy {

    abstract Survey getRenderSurvey(Question screenQuestion);

    public boolean isDynamicStockQuestion(String uid){
        return false;
    }

    abstract boolean isStockSurvey(Survey survey);

    abstract String getMalariaProgram();
}
