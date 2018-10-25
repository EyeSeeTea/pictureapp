package org.eyeseetea.malariacare.services.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;

import java.util.List;

public class SurveyServiceStrategy extends ASurveyServiceStrategy {

    public static List<SurveyDB> getUnsentSurveys(String programUId){
        List<SurveyDB> surveyDBs = SurveyDB.getAllSurveysByProgram(programUId);
        return surveyDBs;
    }
}
