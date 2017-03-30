package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyName;

import android.content.Context;
import android.content.res.Resources;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Program_Table;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey_Table;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.Date;

public class TreatmentQueries {

    public static String getTreatmentError() {
        return PreferencesState.getInstance().getContext().getResources().getResourceName(
                R.string.error_no_treatment);
    }

    public static String getPqTitleDose(float dose) {
        return getTitleDose(dose,
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.drugs_referral_Pq_review_title));
    }

    public static String getCqTitleDose(float dose) {
        return getTitleDose(dose,
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.drugs_referral_Cq_review_title));
    }

    public static String getTitleDose(float dose, String drug) {
        return String.format(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.drugs_dose_of_drug_review_title),
                dose, drug);
    }

    public static boolean isRdtQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(R.string.rdtQuestionUID));
    }


    public static boolean isSevereSymtomsQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.severeSymtomsQuestionUID));
    }


    public static boolean isDynamicStockQuestion(String uid) {
        if (uid != null) {
            return uid.equals(PreferencesState.getInstance().getContext().getString(
                    R.string.dynamicStockQuestionUID));
        }
        return false;
    }

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


    public static boolean isCq(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.cqQuestionUID));
    }


    public static boolean isPq(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .pqQuestionUID));
    }


    public static boolean isDynamicTreatmentQuestion(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.dynamicTreatmentHideQuestionUID));
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


    public static boolean isStockQuestion(Question question) {
        if (question.getHeader() != null && question.getHeader().getName().equals("Stock")) {
            return true;
        }
        return false;
    }


    public static Question getDynamicTreatmentQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentQuestionUID));
    }


    public static Question getTreatmentDiagnosisVisibleQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.treatmentDiagnosisVisibleQuestion));
    }


    public static Question getStockPqQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.stockPqQuestionUID));
    }


    public static Question getPqQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.pqQuestionUID));
    }


    public static Question getAlternativePqQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.alternativePqQuestionUID));
    }


    public static boolean isAgeQuestion(Question question) {
        return question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.ageQuestionUID));
    }


    public static boolean isACT6Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID))) {
            return true;
        }
        return false;
    }


    public static boolean isACT12Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID))) {
            return true;
        }
        return false;
    }


    public static Question getDynamicTreatmentHideQuestion() {
        return Question.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentHideQuestionUID));
    }


    public static boolean isACT18Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID))) {
            return true;
        }
        return false;
    }


    public static boolean isACT24Question(Question question) {
        if (question != null && question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }


    public static Question getDynamicStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicStockQuestionUID));
    }


    public static Question getTreatmentQuestionForTag(Object tag) {
        Resources resources = PreferencesState.getInstance().getContext().getResources();
        if (isPq(((Question) tag).getUid())) {
            return Question.findByUID(resources.getString(R.string.stockPqQuestionUID));
        } else {
            return Question.findByUID(
                    resources.getString(R.string.dynamicTreatmentHideQuestionUID));
        }
    }


    public static boolean isSexPregnantQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.sexPregnantQuestionUID));
    }


    public static Option getOptionTreatmentYesCode() {
        return Option.findByName(PreferencesState.getInstance().getContext().getString(
                R.string.dynamic_treatment_yes_code));
    }

    public static boolean isStockRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .stockRDTQuestionUID));
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

    public static Question getCqQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.cqQuestionUID));
    }

    public static boolean isACT24(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID));
    }

    public static boolean isACT18(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID));
    }

    public static boolean isACT12(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID));
    }

    public static boolean isACT6(String uid_question) {
        return uid_question.equals(
                PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID));
    }

    public static boolean isACT(String uid_question) {
        return isACT6(uid_question) || isACT12(uid_question) || isACT18(uid_question) || isACT24(
                uid_question);
    }

    public static org.eyeseetea.malariacare.data.database.model.Survey getStockSurveyWithEventDate(
            Date eventDate) {
        Context context = PreferencesState.getInstance().getContext();

        return new Select().from(org.eyeseetea.malariacare.data.database.model.Survey.class).as(
                surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Survey_Table.event_date.withTable(surveyAlias)
                        .eq(eventDate))
                .and(Program_Table.uid_program.withTable(programAlias).is(
                        context.getString(R.string.stockProgramUID))).querySingle();

    }
}
