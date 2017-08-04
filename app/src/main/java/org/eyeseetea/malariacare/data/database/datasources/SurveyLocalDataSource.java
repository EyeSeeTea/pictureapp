package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.ArrayList;
import java.util.List;

public class SurveyLocalDataSource implements ISurveyRepository {
    @Override
    public List<Survey> getLastSentSurveys(int count) {
        List<Survey> surveys = new ArrayList<>();

        List<SurveyDB> surveysInDB =
                SurveyDB.getAllHideAndSentSurveys(
                        count);

        for (SurveyDB surveyDBDB : surveysInDB) {
            Survey survey = new Survey(surveyDBDB.getEventDate());

            surveys.add(survey);
        }

        return surveys;
    }

    @Override
    public void deleteSurveys() {
        List<SurveyDB> surveyDBs =
                SurveyDB.getAllSurveys();
        for (SurveyDB surveyDB : surveyDBs) {
            surveyDB.delete();
        }
    }
}
