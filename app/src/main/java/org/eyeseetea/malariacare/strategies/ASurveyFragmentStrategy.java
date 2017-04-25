package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;

public abstract class ASurveyFragmentStrategy {

    abstract Survey getRenderSurvey(Question screenQuestion);

    public boolean isDynamicStockQuestion(String uid) {
        return false;
    }

    abstract boolean isStockSurvey(Survey survey);

    abstract String getMalariaProgram();

    public void onResume() {
        if (Session.getMalariaSurvey() != null) {
            Session.getMalariaSurvey().getValuesFromDB();
        }
    }

    public boolean areActiveSurveysInQuarantine() {
        Survey survey = Session.getMalariaSurvey();
        if (survey != null && survey.isQuarantine()) {
            return true;
        }
        return false;
    }
}
