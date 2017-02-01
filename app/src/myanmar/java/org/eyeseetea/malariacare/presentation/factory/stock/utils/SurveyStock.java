package org.eyeseetea.malariacare.presentation.factory.stock.utils;

import org.eyeseetea.malariacare.database.model.Survey;

/**
 * Created by manuel on 27/12/16.
 */

public class SurveyStock {

    private Survey mSurvey;

    public SurveyStock(Survey survey) {
        mSurvey = survey;
    }

    public Survey getSurvey() {
        return mSurvey;
    }

//TODO
}
