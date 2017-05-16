package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.ArrayList;
import java.util.List;

public class SurveyLocalDataSource implements ISurveyRepository {
    @Override
    public List<Survey> getLastSentSurveys(int count) {
        List<Survey> surveys = new ArrayList<>();

        List<org.eyeseetea.malariacare.data.database.model.Survey> surveysInDB =
                org.eyeseetea.malariacare.data.database.model.Survey.getAllHideAndSentSurveys(
                        count);

        for (org.eyeseetea.malariacare.data.database.model.Survey surveyDB : surveysInDB) {
            Survey survey = new Survey(surveyDB.getEventDate());

            surveys.add(survey);
        }

        return surveys;
    }

    @Override
    public void deleteSurveys() {
        List<org.eyeseetea.malariacare.data.database.model.Survey> surveys =
                org.eyeseetea.malariacare.data.database.model.Survey.getAllSurveys();
        for (org.eyeseetea.malariacare.data.database.model.Survey survey : surveys) {
            survey.delete();
        }
    }
}
