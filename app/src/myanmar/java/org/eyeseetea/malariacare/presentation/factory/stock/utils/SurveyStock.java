package org.eyeseetea.malariacare.presentation.factory.stock.utils;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;

import java.util.List;

/**
 * Created by manuel on 27/12/16.
 */

public class SurveyStock {

    private SurveyDB mSurvey;
    private int[] surveyValues;
    public static final int RDT_VALUE = 0,
            ACT6_VALUE = 1,
            ACT12_VALUE = 2,
            ACT18_VALUE = 3,
            ACT24_VALUE = 4,
            CQ_VALUE = 5,
            PQ_VALUE = 6;

    public SurveyStock(SurveyDB survey) {
        mSurvey = survey;
        surveyValues = new int[7];
        createSurveyValues();
    }


    public SurveyDB getSurvey() {
        return mSurvey;
    }

    public int[] getSurveyValues() {
        return surveyValues;
    }

    private void createSurveyValues() {
        List<ValueDB> values = mSurvey.getValuesFromDB();
        for (ValueDB value : values) {
            if(value.getQuestionDB()==null) {
                continue;//ignore values without question for example control dataelements
            }
            if (TreatmentQueries.isStockRDT(value.getQuestionDB().getUid())) {
                surveyValues[RDT_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isACT6(value.getQuestionDB().getUid())) {
                surveyValues[ACT6_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isACT12(value.getQuestionDB().getUid())) {
                surveyValues[ACT12_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isACT18(value.getQuestionDB().getUid())) {
                surveyValues[ACT18_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isACT24(value.getQuestionDB().getUid())) {
                surveyValues[ACT24_VALUE] =(int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isPq(value.getQuestionDB().getUid())) {
                surveyValues[PQ_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            } else if (TreatmentQueries.isCq(value.getQuestionDB().getUid())) {
                surveyValues[CQ_VALUE] = (int) Math.ceil(Double.parseDouble(value.getValue()));
            }
        }
    }

    public boolean isIssueSurvey() {
        return mSurvey.isIssueSurvey();
    }

}
