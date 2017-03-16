package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;

public class QuestionStrategy {

    @Override
    public Question getTreatmentQuestionForTag(Object tag) {
        return null;
    }

    @Override
    public boolean isTreatmentQuestion(String uid_question) {
        return false;
    }

    @Override
    public boolean isOutStockQuestion(String uid_question) {
        return false;
    }

    @Override
    public boolean isACT6(String uid_question) {
        return false;
    }

    @Override
    public boolean isACT12(String uid_question) {
        return false;
    }

    @Override
    public boolean isACT18(String uid_question) {
        return false;
    }

    @Override
    public boolean isACT24(String uid_question) {
        return false;
    }

    @Override
    public boolean isACT(String uid_question) {
        return false;
    }

    @Override
    public boolean isPq(String uid_question) {
        return false;
    }

    @Override
    public boolean isCq(String uid_question) {
        return false;
    }

    @Override
    public boolean isDynamicTreatmentQuestion(String uid_question) {
        return false;
    }

    @Override
    public boolean isStockQuestion(Question question) {
        return false;
    }

    @Override
    public Question getPqQuestion() {
        return null;
    }

    @Override
    public Question getAlternativePqQuestion() {
        return null;
    }
}
