package org.eyeseetea.malariacare.data.model;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.Date;

public class QuestionStrategy {


    public static Survey getStockSurveyWithEventDate(Date event_date) {
        return null;
    }
    public static boolean isTreatmentQuestion(String uid_question) {
        return false;
    }

    public static boolean isOutStockQuestion(String uid_question) {
        return false;
    }

    public static boolean isACT6(String uid_question) {
        return false;
    }

    public static boolean isACT12(String uid_question) {
        return false;
    }

    public static boolean isACT18(String uid_question) {
        return false;
    }

    public static boolean isACT24(String uid_question) {        return false;
    }

    public static boolean isACT(String uid_question) {        return false;}


    public static boolean isPq(String uid_question) {        return false;}

    public static boolean isCq(String uid_question) {        return false;}

    public static boolean isDynamicTreatmentQuestion(String uid_question) {        return false;}

    public static Question getACT6Question() {        return null;}

    public static Question getACT12Question() {        return null;}

    public static Question getACT18Question() { return null;}

    public static Question getACT24Question() { return null;}

    public static Question getOutOfStockQuestion() { return null;}

    public static boolean isStockQuestion(Question question ) {
        return false;
    }

    public static Question getDynamicTreatmentQuestion() {
        return  null;
    }

    public static Question getTreatmentDiagnosisVisibleQuestion() {
        return null;
    }

    public static Question getStockPqQuestion() {
        return  null;
    }

    public static Question getPqQuestion() {
        return null;
    }

    public static Question getAlternativePqQuestion() {
        return null;
    }
}
