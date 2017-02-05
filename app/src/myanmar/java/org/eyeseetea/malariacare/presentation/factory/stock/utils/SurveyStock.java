package org.eyeseetea.malariacare.presentation.factory.stock.utils;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;

import java.util.List;

/**
 * Created by manuel on 27/12/16.
 */

public class SurveyStock {

    private Survey mSurvey;
    private float[] surveyValues;
    public static final int RDT_VALUE = 0,
            ACT6_VALUE = 1,
            ACT12_VALUE = 2,
            ACT18_VALUE = 3,
            ACT24_VALUE = 4,
            CQ_VALUE = 5,
            PQ_VALUE = 6;

    public SurveyStock(Survey survey) {
        mSurvey = survey;
        surveyValues = new float[7];
        createSurveyValues();
    }


    public Survey getSurvey() {
        return mSurvey;
    }

    public float[] getSurveyValues() {
        return surveyValues;
    }

    private void createSurveyValues() {
        List<Value> values = mSurvey.getValuesFromDB();
        for (Value value : values) {
            if (value.getQuestion().isStockRDT()) {
                surveyValues[RDT_VALUE] = Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isACT6()) {
                surveyValues[ACT6_VALUE] = Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isACT12()) {
                surveyValues[ACT12_VALUE] = Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isACT18()) {
                surveyValues[ACT18_VALUE] = Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isACT24()) {
                surveyValues[ACT24_VALUE] =Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isPq()) {
                surveyValues[PQ_VALUE] = Float.parseFloat(value.getValue());
            } else if (value.getQuestion().isCq()) {
                surveyValues[CQ_VALUE] = Float.parseFloat(value.getValue());
            }
        }
    }

    public boolean isExpenseSurvey() {
        return mSurvey.isExpenseSurvey();
    }

}
