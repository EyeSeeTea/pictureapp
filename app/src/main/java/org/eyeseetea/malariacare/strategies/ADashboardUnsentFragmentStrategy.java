package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;

public abstract class ADashboardUnsentFragmentStrategy {
    public static void deleteSurvey(SurveyDB surveyDB) {
        surveyDB.delete();
    }
}
