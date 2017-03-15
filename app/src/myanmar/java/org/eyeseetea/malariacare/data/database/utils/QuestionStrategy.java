package org.eyeseetea.malariacare.data.database.utils;


import android.content.Context;
import android.content.res.Resources;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;

public class QuestionStrategy implements IQuestionStrategy {
    @Override
    public boolean isRdtQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(R.string.rdtQuestionUID));
    }

    @Override
    public boolean isSevereSymtomsQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.severeSymtomsQuestionUID));
    }
    @Override
    public boolean isTreatmentQuestion(String uid_question) {
        Context context = PreferencesState.getInstance().getContext();
        if (uid_question.equals(context.getString(R.string.ageQuestionUID))
                || uid_question.equals(
                context.getString(R.string.ageQuestionUID)) || uid_question.equals(
                context.getString(R.string.severeSymtomsQuestionUID))
                || uid_question.equals(context.getString(R.string.rdtQuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOutStockQuestion(String uid_question) {
        Context context = PreferencesState.getInstance().getContext();
        if (uid_question.equals(context.getString(R.string.outOfStockQuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isACT6(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID));
    }

    @Override
    public boolean isACT12(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID));
    }

    @Override
    public boolean isACT18(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID));
    }

    @Override
    public boolean isACT24(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID));
    }

    @Override
    public boolean isACT(String uid_question) {
        return isACT6(uid_question) || isACT12(uid_question) || isACT18(uid_question) || isACT24(uid_question);
    }

    @Override
    public boolean isCq(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.cqQuestionUID));
    }

    @Override
    public boolean isPq(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .pqQuestionUID));
    }

    @Override
    public boolean isDynamicTreatmentQuestion(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.dynamicTreatmentHideQuestionUID));
    }

    @Override
    public Question getACT6Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act6QuestionUID));
    }

    @Override
    public Question getACT12Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act12QuestionUID));
    }

    @Override
    public Question getACT18Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act18QuestionUID));
    }

    @Override
    public Question getACT24Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act24QuestionUID));
    }

    @Override
    public Question getOutOfStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.outOfStockQuestionUID));
    }

    @Override
    public boolean isStockQuestion(Question question) {
        if (question.getHeader() != null && question.getHeader().getName().equals("Stock")) {
            return true;
        }
        return false;
    }

    @Override
    public Question getDynamicTreatmentQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentQuestionUID));
    }

    @Override
    public Question getTreatmentDiagnosisVisibleQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.treatmentDiagnosisVisibleQuestion));
    }

    @Override
    public Question getStockPqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.stockPqQuestionUID));
    }

    @Override
    public Question getPqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.pqQuestionUID));
    }

    @Override
    public Question getAlternativePqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.alternativePqQuestionUID));
    }

    @Override
    public boolean isAgeQuestion(Question question) {
        return question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.ageQuestionUID));
    }

    @Override
    public boolean isACT6Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isACT12Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public Question getDynamicTreatmentHideQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentHideQuestionUID));
    }

    @Override
    public boolean isACT18Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isACT24Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }

    @Override
    public Question getDynamicStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicStockQuestionUID));
    }

    @Override
    public Question getTreatmentQuestionForTag(Object tag) {
        Resources resources = PreferencesState.getInstance().getContext().getResources();
        if (isPq(((Question) tag).getUid())) {
            return Question.findByUID(resources.getString(R.string.stockPqQuestionUID));
        } else {
            return Question.findByUID(
                    resources.getString(R.string.dynamicTreatmentHideQuestionUID));
        }
    }

    @Override
    public boolean isSexPregnantQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.sexPregnantQuestionUID));
    }

    @Override
    public Option getOptionTreatmentYesCode() {
        return Option.findByCode(PreferencesState.getInstance().getContext().getString(
                R.string.dynamic_treatment_yes_code));
    }

    public boolean isRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .rdtQuestionUID));
    }

    public boolean isStockRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .stockRDTQuestionUID));
    }

    public boolean isInvalidCounter(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .confirmInvalidQuestionUID));
    }

    public boolean isACTQuestion(Question question) {
        Context context = PreferencesState.getInstance().getContext();
        if (question.getUid().equals(context.getString(R.string.act6QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act12QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act18QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isPq(Question question) {
        if (question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.pqQuestionUID))) {
            return true;
        }
        return false;
    }
    public boolean isCq(Question question) {
        if (question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.cqQuestionUID))) {
            return true;
        }
        return false;
    }
    public Question getTreatmentQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicTreatmentHideQuestionUID));
    }

    public boolean isInvalidRDTQuestion(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.confirmInvalidQuestionUID));
    }

    public Question getRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.rdtQuestionUID));
    }

    public Question getStockRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.stockRDTQuestionUID));
    }

    public Question getInvalidCounterQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.confirmInvalidQuestionUID));
    }

    public Question getCqQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.cqQuestionUID));
    }
}
