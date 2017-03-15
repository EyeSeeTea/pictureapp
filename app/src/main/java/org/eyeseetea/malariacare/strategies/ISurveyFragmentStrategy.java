package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.Date;

public interface ISurveyFragmentStrategy {

    Survey getRenderSurvey(Question screenQuestion);

    boolean isDynamicStockQuestion(Question screenQuestion);

    boolean isStockSurvey(Survey survey);

    String getMalariaProgram();


    Survey getStockSurveyWithEventDate(Date event_date);

    String getTitleDose(float dose, String drug);

    String getTreatmentError();

    String getPqTitleDose(float dose);

    String getCqTitleDose(float dose);
}
