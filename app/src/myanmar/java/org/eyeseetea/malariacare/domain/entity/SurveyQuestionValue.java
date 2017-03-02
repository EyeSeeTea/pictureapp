package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;

public class SurveyQuestionValue {
    private Survey mSurvey;
    private static final int RDT = 1,
            ACT6 = 2,
            ACT12 = 3,
            ACT18 = 4,
            ACT24 = 5,
            PQ = 6,
            CQ = 7,
            OUT_STOCK = 8;


    public SurveyQuestionValue(Survey survey) {
        mSurvey = survey;
        mSurvey.getValuesFromDB();
    }

    public String getRDTValue() {
        return getValueQuestion(RDT);
    }

    public String getACT6Value() {
        return getValueQuestion(ACT6);
    }

    public String getACT12Value() {
        return getValueQuestion(ACT12);
    }

    public String getACT18Value() {
        return getValueQuestion(ACT18);
    }

    public String getACT24Value() {
        return getValueQuestion(ACT24);
    }

    public String getPqValue() {
        return getValueQuestion(PQ);
    }

    public String getCqValue() {
        return getValueQuestion(CQ);
    }

    public String getOutStockValue() {
        return getValueQuestion(OUT_STOCK);
    }


    public String getValueQuestion(int question) {
        for (Value value : mSurvey.getValues()) {
            if (value.getQuestion() == null) return "0";
            switch (question) {
                case RDT:
                    if (value.getQuestion().isStockRDT()) {
                        return value.getValue();
                    }
                    break;
                case ACT6:
                    if (value.getQuestion().isACT6()) {
                        return value.getValue();
                    }
                    break;
                case ACT12:
                    if (value.getQuestion().isACT12()) {
                        return value.getValue();
                    }
                    break;
                case ACT18:
                    if (value.getQuestion().isACT18()) {
                        return value.getValue();
                    }
                    break;
                case ACT24:
                    if (value.getQuestion().isACT24()) {
                        return value.getValue();
                    }
                    break;
                case PQ:
                    if (value.getQuestion().isPq()) {
                        return value.getValue();
                    }
                    break;
                case CQ:
                    if (value.getQuestion().isCq()) {
                        return value.getValue();
                    }
                    break;
                case OUT_STOCK:
                    if (value.getQuestion().isOutStockQuestion()) {
                        return value.getValue();
                    }
                    break;
            }
        }
        return "0";
    }

}
