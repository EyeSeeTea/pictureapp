package org.eyeseetea.malariacare.services.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;

import java.util.List;

public class SurveyServiceStrategy extends ASurveyServiceStrategy {

    public static List<SurveyDB> getUnsentSurveys(String programUId){
        List<SurveyDB> surveyDBs = SurveyDB.getAllUnsentMalariaSurveys(programUId);
        List<SurveyDB> sentSurveys = SurveyDB.getAllSentMalariaSurveys(programUId);
        surveyDBs.addAll(sentSurveys);
        return surveyDBs;
    }
}
