package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.ArrayList;
import java.util.List;

public class SurveyLocalDataSource implements ISurveyRepository {
    @Override
    public List<Survey> getLastSentSurveys(int count) {
        List<Survey> surveys = new ArrayList<>();

        List<SurveyDB> surveysInDB =
                SurveyDB.getSentSurveys(
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

    @Override
    public void getUnsentSurveys(IDataSourceCallback<List<Survey>> callback) {
        List<Survey> unsentSurveys = getUnsentSurveys();
        callback.onSuccess(unsentSurveys);
    }

    @Override
    public List<Survey> getUnsentSurveys() {
        List<Survey> unsentSurveys = new ArrayList<>();
        for (SurveyDB surveyDB : SurveyDB.getAllUnsentSurveys()) {
            Survey survey = new Survey(surveyDB.getEventDate());
            unsentSurveys.add(survey);
        }
        return unsentSurveys;
    }

    @Override
    public List<Survey> getAllQuarantineSurveys() {
        List<SurveyDB> surveyDBs = SurveyDB.getAllQuarantineSurveys();
        List<Survey> surveys = new ArrayList<>();
        for(SurveyDB surveyDB : surveyDBs){
            surveys.add(new Survey(surveyDB.getId_survey(), surveyDB.getStatus(), null));
        }
        return surveys;
    }

    @Override
    public List<Survey> getAllCompletedSurveys() {
        List<Survey> completedSurveys = new ArrayList<>();
        for (SurveyDB surveyDB : SurveyDB.getAllCompletedSurveys()) {
            Survey survey = new Survey(surveyDB.getEventDate());
            completedSurveys.add(survey);
        }
        return completedSurveys;
    }

    @Override
    public Survey save(Survey survey) {
        SurveyDB surveyDB = SurveyDB.findById(survey.getId());
        if (surveyDB == null) {
            OrgUnitDB orgUnitDB = null;
            if (survey.getOrganisationUnit() != null) {
                orgUnitDB = OrgUnitDB.findByUID(survey.getOrganisationUnit().getUid());
            }
            UserDB userDB = null;
            if (survey.getUserAccount() != null) {
                userDB = UserDB.findByUID(survey.getUserAccount().getUserUid());
            }
            surveyDB = new SurveyDB(orgUnitDB, ProgramDB.getProgram(survey.getProgram().getId()),
                    userDB, survey.getType());
            surveyDB.save();
        }
        surveyDB.setStatus(survey.getStatus());
        surveyDB.update();
        survey.setId(surveyDB.getId_survey());
        return survey;
    }
}
