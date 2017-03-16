package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.Date;

public class QuestionStrategy{

    public boolean isTreatmentQuestion(String uid_question) {
        return false;
    }


    public boolean isOutStockQuestion(String uid_question) {
        return false;
    }
 
    public boolean isACT(String uid_question) {
        return false;
    }


    public boolean isPq(String uid_question) {
        return false;
    }


    public boolean isDynamicTreatmentQuestion(String uid_question) {
        return false;
    }


    public boolean isStockQuestion(Question question) {
        return false;
    }

}