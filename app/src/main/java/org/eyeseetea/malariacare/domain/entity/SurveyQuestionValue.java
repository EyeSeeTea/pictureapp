package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;

public class SurveyQuestionValue {
    private Survey mSurvey;
    private static final int RDT = 1,
            ACT6 = 2,
            ACT12 = 3,
            ACT18 = 4,
            ACT24 = 5,
            PQ = 6,
            CQ = 7;

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


    public String getValueQuestion(int question) {
        for (Value value : mSurvey.getValues()) {
            switch (question) {
                case RDT:
                    if (value.getQuestion().isRDT()) {
                        return value.getValue();
                    }
                case ACT6:
                    if (value.getQuestion().isACT6()) {
                        return value.getValue();
                    }
                case ACT12:
                    if (value.getQuestion().isACT12()) {
                        return value.getValue();
                    }
                case ACT18:
                    if (value.getQuestion().isACT18()) {
                        return value.getValue();
                    }
                case ACT24:
                    if (value.getQuestion().isACT24()) {
                        return value.getValue();
                    }
                case PQ:
                    if (value.getQuestion().isPq()) {
                        return value.getValue();
                    }
                case CQ:
                    if (value.getQuestion().isCq()) {
                        return value.getValue();
                    }
            }
        }
        return "0";
    }

}
