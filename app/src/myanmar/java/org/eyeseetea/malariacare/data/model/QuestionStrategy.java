package org.eyeseetea.malariacare.data.model;

import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyName;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Program_Table;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Survey_Table;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.Date;

public class QuestionStrategy {

    public static boolean isTreatmentQuestion(String uid_question) {
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

    public static boolean isOutStockQuestion(String uid_question) {
        Context context = PreferencesState.getInstance().getContext();
        if (uid_question.equals(context.getString(R.string.outOfStockQuestionUID))) {
            return true;
        }
        return false;
    }

    public static boolean isRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.rdtQuestionUID));
    }

    public static boolean isStockRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.stockRDTQuestionUID));
    }

    public static boolean isInvalidCounter(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.confirmInvalidQuestionUID));
    }

    public static boolean isACT6(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID));
    }

    public static boolean isACT12(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID));
    }

    public static boolean isACT18(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID));
    }

    public static boolean isACT24(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID));
    }

    public static boolean isACT(String uid_question) {
        return isACT6(uid_question) || isACT12(uid_question) || isACT18(uid_question) || isACT24(uid_question);
    }

    public static boolean isCq(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.cqQuestionUID));
    }

    public static boolean isPq(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.pqQuestionUID));
    }

    public static boolean isDynamicTreatmentQuestion(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.dynamicTreatmentHideQuestionUID));
    }

    public static boolean isInvalidRDTQuestion(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.confirmInvalidQuestionUID));
    }
    public static Question getRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.rdtQuestionUID));
    }

    public static Question getStockRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.stockRDTQuestionUID));
    }

    public static Question getInvalidCounterQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.confirmInvalidQuestionUID));
    }

    public static Question getPqQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.pqQuestionUID));
    }

    public static Question getCqQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.cqQuestionUID));
    }

    public static Question getACT6Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act6QuestionUID));
    }

    public static Question getACT12Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act12QuestionUID));
    }

    public static Question getACT18Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act18QuestionUID));
    }

    public static Question getACT24Question() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.act24QuestionUID));
    }

    public static Question getOutOfStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.outOfStockQuestionUID));
    }

    public static boolean isStockQuestion(Question question ) {
        if (question.getHeader() != null && question.getHeader().getName().equals("Stock")) {
            return true;
        }
        return false;
    }

    public static Question getDynamicTreatmentQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.dynamicTreatmentQuestionUID);
    }

    public static Question getDynamicTreatmentQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.dynamicTreatmentQuestionUID);
    }
    public static Question getTreatmentDiagnosisVisibleQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.treatmentDiagnosisVisibleQuestion);
    }
    public static Question getStockPqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.stockPqQuestionUID);
    }
    public static Question getPqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.pqQuestionUID);
    }
    public static Question getAlternativePqQuestion() {
        return  Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.alternativePqQuestionUID);
    }
}
