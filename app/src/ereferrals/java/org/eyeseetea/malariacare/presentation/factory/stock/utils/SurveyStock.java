package org.eyeseetea.malariacare.presentation.factory.stock.utils;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;

import java.util.List;

/**
 * Created by manuel on 27/12/16.
 */

public class SurveyStock {

    private Survey mSurvey;
    private int[] surveyValues;
    public static final int RDT_VALUE = 0,
            ACT6_VALUE = 1,
            ACT12_VALUE = 2,
            ACT18_VALUE = 3,
            ACT24_VALUE = 4,
            CQ_VALUE = 5,
            PQ_VALUE = 6;

    public SurveyStock(Survey survey) {
        mSurvey = survey;
        surveyValues = new int[7];
        createSurveyValues();
    }


    public Survey getSurvey() {
        return mSurvey;
    }

    public int[] getSurveyValues() {
        return surveyValues;
    }

    private void createSurveyValues() {
        List<Value> values = mSurvey.getValuesFromDB();
        for (Value value : values) {
            if(value.getQuestion()==null) {
                continue;//ignore values without question for example control dataelements
            }
            if (value.getQuestion().isStockRDT()) {
                surveyValues[RDT_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isACT6()) {
                surveyValues[ACT6_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isACT12()) {
                surveyValues[ACT12_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isACT18()) {
                surveyValues[ACT18_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isACT24()) {
                surveyValues[ACT24_VALUE] =(int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isPq()) {
                surveyValues[PQ_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (value.getQuestion().isCq()) {
                surveyValues[CQ_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            }
        }
    }

    public boolean isIssueSurvey() {
        return mSurvey.isIssueSurvey();
    }

}
