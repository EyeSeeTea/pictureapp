package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.strategies.ASurveyLocalDataSourceStrategy;
import org.eyeseetea.malariacare.data.database.datasources.strategies.SurveyLocalDataSourceStrategy;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyLocalDataSource implements ISurveyRepository {

    private ASurveyLocalDataSourceStrategy mSurveyLocalDataSourceStrategy;

    private final Map<Long, ProgramDB> programDBS = new HashMap<>();
    private final Map<Long, OrgUnitDB> orgUnitDBS = new HashMap<>();
    private final Map<Long, QuestionDB> questionsDBs = new HashMap<>();
    private final Map<Long, OptionDB> optionDBs = new HashMap<>();
    private UserDB userDB;

    private void loadDependencies() {
        if (programDBS.size() == 0) {
            for (ProgramDB programDB : ProgramDB.getAllPrograms()) {
                programDBS.put(programDB.getId_program(), programDB);
            }
        }

        if (orgUnitDBS.size() == 0) {
            for (OrgUnitDB orgUnitDB : OrgUnitDB.getAllOrgUnit()) {
                orgUnitDBS.put(orgUnitDB.getId_org_unit(), orgUnitDB);
            }
        }

        if (questionsDBs.size() == 0) {
            for (QuestionDB questionDB : QuestionDB.getAllQuestions()) {
                questionsDBs.put(questionDB.getId_question(), questionDB);
            }
        }

        if (optionDBs.size() == 0) {
            for (OptionDB optionDB : OptionDB.getAllOptions()) {
                optionDBs.put(optionDB.getId_option(), optionDB);
            }
        }

        if (userDB == null) {
            userDB = UserDB.getLoggedUser();
        }
    }

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
            surveys.add(mapSurvey(surveyDB));
        }
        return surveys;
    }

    @Override
    public List<Survey> getAllCompletedSurveys() {
        List<Survey> completedSurveys = new ArrayList<>();
        for (SurveyDB surveyDB : SurveyDB.getAllCompletedSentSurveys()) {
            completedSurveys.add(mapSurvey(surveyDB));
        }
        return completedSurveys;
    }

    @Override
    public List<Survey> getAllCompletedSentSurveys() {
        List<Survey> completedSurveys = new ArrayList<>();
        for (SurveyDB surveyDB : SurveyDB.getAllCompletedSentSurveys()) {
            completedSurveys.add(mapSurvey(surveyDB));
        }
        return completedSurveys;
    }

    @Override
    public long save(Survey survey) {
        SurveyDB surveyDB = SurveyDB.findById(survey.getId());
        if (surveyDB == null) {
            OrgUnitDB orgUnitDB = null;
            if (survey.getOrgUnitUid() != null) {
                orgUnitDB = OrgUnitDB.findByUID(survey.getOrgUnitUid());
            }
            UserDB userDB = null;
            if (survey.getUserUid() != null) {
                userDB = UserDB.findByUID(survey.getUserUid());
            }
            surveyDB = new SurveyDB(orgUnitDB, ProgramDB.getProgram(survey.getProgramUid()),
                    userDB, survey.getType());
            surveyDB.save();
        }
        surveyDB.setStatus(survey.getStatus());
        surveyDB.update();
        setSurveyOnSession(surveyDB);
        return surveyDB.getId_survey();
    }

    private void setSurveyOnSession(SurveyDB surveyDB) {
        if(surveyDB.getType() == Constants.SURVEY_NO_TYPE){
            Session.setMalariaSurveyDB(surveyDB);
        }
    }


    public List<Survey> getSurveysByProgram(String uid) {
        List<SurveyDB> surveysDB = SurveyDB.getAllSurveysByProgram(uid);
        List<Survey> surveys = new ArrayList<>();

        for (SurveyDB surveyDB : surveysDB) {
            surveys.add(mapSurvey(surveyDB));
        }
        return surveys;
    }

    @Override
    public Survey createNewSurvey() {
        mSurveyLocalDataSourceStrategy = new SurveyLocalDataSourceStrategy();
        return mSurveyLocalDataSourceStrategy.createNewSurvey();
    }

    @Override
    public void removeInProgress() {
        SurveyDB.removeInProgress();
    }

    @Override
    public void deleteSurveyByUid(String surveyUid) {
        SurveyDB surveyDB = SurveyDB.findByUid(surveyUid);

        if (surveyDB != null) {
            surveyDB.delete();
        }
    }

    private Survey mapSurvey(SurveyDB surveyDB) {
        loadDependencies();

        List<Value> values = getValuesFromSurvey(surveyDB);

        String programUid = null;
        String orgUnitUid = null;

        if (programDBS.size() > 0){
            programUid = programDBS.get(surveyDB.getId_program_fk()).getUid();
        }

        if (orgUnitDBS.size() > 0){
            orgUnitUid = orgUnitDBS.get(surveyDB.getId_org_unit_fk()).getUid();
        }

        Survey survey = new Survey(
                surveyDB.getId_survey(),
                surveyDB.getEventUid(),
                surveyDB.getVoucherUid(),
                surveyDB.getStatus(),
                null,
                surveyDB.getEventDate(),
                programUid,
                orgUnitUid,
                userDB.getUid(),
                surveyDB.getType(),
                values);

        return survey;
    }

    private List<Value> getValuesFromSurvey(SurveyDB surveyDB) {
        List<Value> values = new ArrayList<>();
        List<ValueDB> valueDBS = ValueDB.listAllBySurvey(surveyDB);

        for (ValueDB valueDB : valueDBS) {
            QuestionDB questionDB = questionsDBs.get(valueDB.getId_question_fk());

            String questionUid = questionDB.getUid();
            String optionCode;

            Value value;

            if (valueDB.getId_option_fk() != null) {
                optionCode = optionDBs.get(valueDB.getId_option_fk()).getCode();
                value = new Value(valueDB.getValue(), questionUid, optionCode);
            } else {
                value = new Value(valueDB.getValue(), questionUid);
            }

            if (questionDB.isImportant()) {
                value.setVisibility(Question.Visibility.IMPORTANT);
            } else if (questionDB.isVisible()) {
                value.setVisibility(Question.Visibility.VISIBLE);
            } else {
                value.setVisibility(Question.Visibility.INVISIBLE);
            }

            values.add(value);
        }
        return values;
    }

}
