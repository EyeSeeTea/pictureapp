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
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB_Table;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB_Table;
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

    public static QuestionDB getACT6Question() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.act6QuestionUID));
    }


    public static QuestionDB getACT12Question() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.act12QuestionUID));
    }


    public static QuestionDB getACT18Question() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.act18QuestionUID));
    }


    public static QuestionDB getACT24Question() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.act24QuestionUID));
    }


    public static QuestionDB getOutOfStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.outOfStockQuestionUID));
    }


    public static boolean isStockQuestion(QuestionDB questionDB) {
        if (questionDB.getHeaderDB() != null && questionDB.getHeaderDB().getName().equals("Stock")) {
            return true;
        }
        return false;
    }


    public static QuestionDB getDynamicTreatmentQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentQuestionUID));
    }


    public static QuestionDB getTreatmentDiagnosisVisibleQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.treatmentDiagnosisVisibleQuestion));
    }


    public static QuestionDB getStockPqQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.stockPqQuestionUID));
    }


    public static QuestionDB getPqQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.pqQuestionUID));
    }


    public static QuestionDB getAlternativePqQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.alternativePqQuestionUID));
    }


    public static boolean isAgeQuestion(QuestionDB questionDB) {
        return questionDB.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.ageQuestionUID));
    }


    public static boolean isACT6Question(QuestionDB questionDB) {
        if (questionDB != null && questionDB.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID))) {
            return true;
        }
        return false;
    }


    public static boolean isACT12Question(QuestionDB questionDB) {
        if (questionDB != null && questionDB.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID))) {
            return true;
        }
        return false;
    }


    public static QuestionDB getDynamicTreatmentHideQuestion() {
        return QuestionDB.findByUID(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.dynamicTreatmentHideQuestionUID));
    }


    public static boolean isACT18Question(QuestionDB questionDB) {
        if (questionDB != null && questionDB.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID))) {
            return true;
        }
        return false;
    }


    public static boolean isACT24Question(QuestionDB questionDB) {
        if (questionDB != null && questionDB.getUid().equals(
                PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }


    public static QuestionDB getDynamicStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.dynamicStockQuestionUID));
    }


    public static QuestionDB getTreatmentQuestionForTag(Object tag) {
        Resources resources = PreferencesState.getInstance().getContext().getResources();
        if (isPq(((QuestionDB) tag).getUid())) {
            return QuestionDB.findByUID(resources.getString(R.string.stockPqQuestionUID));
        } else {
            return QuestionDB.findByUID(
                    resources.getString(R.string.dynamicTreatmentHideQuestionUID));
        }
    }


    public static boolean isSexPregnantQuestion(String uid) {
        return uid.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.sexPregnantQuestionUID));
    }


    public static OptionDB getOptionTreatmentYesCode() {
        return OptionDB.findByName(PreferencesState.getInstance().getContext().getString(
                R.string.dynamic_treatment_yes_code));
    }

    public static boolean isStockRDT(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string
                .stockRDTQuestionUID));
    }

    public static QuestionDB getRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.rdtQuestionUID));
    }

    public static QuestionDB getStockRDTQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.stockRDTQuestionUID));
    }

    public static QuestionDB getInvalidCounterQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.confirmInvalidQuestionUID));
    }

    public static QuestionDB getCqQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return QuestionDB.findByUID(context.getString(R.string.cqQuestionUID));
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

    public static SurveyDB getStockSurveyWithEventDate(
            Date eventDate) {
        Context context = PreferencesState.getInstance().getContext();

        return new Select().from(SurveyDB.class).as(
                surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .where(SurveyDB_Table.event_date.withTable(surveyAlias)
                        .eq(eventDate))
                .and(ProgramDB_Table.uid_program.withTable(programAlias).is(
                        context.getString(R.string.stockProgramUID))).querySingle();

    }
}
