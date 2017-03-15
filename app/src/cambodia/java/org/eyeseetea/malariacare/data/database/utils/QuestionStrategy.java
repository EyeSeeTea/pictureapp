package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.Date;

public class QuestionStrategy implements IQuestionStrategy {
    @Override
    public boolean isRdtQuestion(String uid) {
        return false;
    }

    @Override
    public boolean isSevereSymtomsQuestion(String uid) {
        return false;
    }

    @Override
    public Question getDynamicStockQuestion() {
        return  null;
    }

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
    public Question getACT6Question() {
        return null;
    }

    @Override
    public Question getACT12Question() {
        return null;
    }

    @Override
    public Question getACT18Question() {
        return null;
    }

    @Override
    public Question getACT24Question() {
        return null;
    }

    @Override
    public Question getOutOfStockQuestion() {
        return null;
    }

    @Override
    public boolean isStockQuestion(Question question) {
        return false;
    }

    @Override
    public Question getDynamicTreatmentQuestion() {
        return null;
    }

    @Override
    public Question getTreatmentDiagnosisVisibleQuestion() {
        return null;
    }

    @Override
    public Question getStockPqQuestion() {
        return null;
    }

    @Override
    public Question getPqQuestion() {
        return null;
    }

    @Override
    public Question getAlternativePqQuestion() {
        return null;
    }

    @Override
    public boolean isACT24Question(Question question) {
        return false;
    }

    @Override
    public boolean isACT18Question(Question question) {
        return false;
    }

    @Override
    public boolean isACT6Question(Question question) {
        return false;
    }

    @Override
    public boolean isACT12Question(Question question) {
        return false;
    }

    @Override
    public boolean isAgeQuestion(Question question) {
        return false;
    }

    @Override
    public boolean isSexPregnantQuestion(String uid) {
        return false;
    }

    @Override
    public Question getDynamicTreatmentHideQuestion() {
        return null;
    }

    @Override
    public Option getOptionTreatmentYesCode() {
        return null;
    }
}