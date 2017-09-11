package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

public class SurveyQuestionTreatmentValue {
    private Survey mSurvey;
    private static final int RDT = 1,
            ACT6 = 2,
            ACT12 = 3,
            ACT18 = 4,
            ACT24 = 5,
            PQ = 6,
            CQ = 7,
            OUT_STOCK = 8;


    public SurveyQuestionTreatmentValue(Survey survey) {
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
        for (Value value : mSurvey.getValues()) {//this values should be get from memory because
            // the treatment options are in memory
            if (value.getQuestion() == null) {
                continue;
            }
            if (!new SurveyFragmentStrategy().isStockSurvey(mSurvey)) {
                if (question == OUT_STOCK) {
                    if (TreatmentQueries.isOutStockQuestion(value.getQuestion().getUid())) {
                        return value.getValue();
                    }
                }
                break;
            } else {
                switch (question) {
                    case RDT:
                        if (TreatmentQueries.isStockRDT(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case ACT6:
                        if (TreatmentQueries.isACT6(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case ACT12:
                        if (TreatmentQueries.isACT12(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case ACT18:
                        if (TreatmentQueries.isACT18(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case ACT24:
                        if (TreatmentQueries.isACT24(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case PQ:
                        if (TreatmentQueries.isPq(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                    case CQ:
                        if (TreatmentQueries.isCq(value.getQuestion().getUid())) {
                            return value.getValue();
                        }
                        break;
                }
            }
        }
        return "0";
    }

}
