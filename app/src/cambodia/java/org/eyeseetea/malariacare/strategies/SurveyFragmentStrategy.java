package org.eyeseetea.malariacare.strategies;


import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;

import java.util.Date;

public class SurveyFragmentStrategy  implements ISurveyFragmentStrategy {

    @Override
    public Survey getRenderSurvey(Question screenQuestion) {
        return Session.getMalariaSurvey();
    }

    @Override
    public String getMalariaProgram() {
        return Program.getFirstProgram().getUid();
    }

    @Override
    public boolean isDynamicStockQuestion(Question screenQuestion) {
        return false;
    }

    @Override
    public boolean isStockSurvey(Survey survey) {
        return false;
    }

    @Override
    public Survey getStockSurveyWithEventDate(Date event_date) {
        return null;
    }

    @Override
    public String getTitleDose(float dose, String drug) {
        return null;
    }

    @Override
    public String getTreatmentError() {
        return null;
    }

    @Override
    public String getPqTitleDose(float dose) {
        return null;
    }

    @Override
    public String getCqTitleDose(float dose) {
        return null;
    }
}