package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragmentStrategy {

    public static SurveyDB getRenderSurvey(QuestionDB screenQuestion) {
        return Session.getMalariaSurveyDB();
    }

    public String getMalariaProgram() {
        return ProgramDB.getFirstProgram().getUid();
    }
    public static SurveyDB getSessionSurveyByQuestion(QuestionDB question) {
        return Session.getMalariaSurveyDB();
    }

    public static void saveValueDDlExtraOperations(ValueDB valueDB, OptionDB option, String uid) {

    }

    public static SurveyDB getSaveValuesDDLSurvey(QuestionDB question) {
        return Session.getMalariaSurveyDB();
    }

    public static void saveValuesText(ValueDB valueDB, String answer,
            QuestionDB question, SurveyDB surveyDB) {
        question.createOrSaveValue(answer, valueDB, surveyDB);
        for (QuestionDB propagateQuestion : question.getPropagationQuestions()) {
            propagateQuestion.createOrSaveValue(answer,
                    ValueDB.findValueFromDatabase(propagateQuestion.getId_question(),
                            Session.getMalariaSurveyDB()), Session.getMalariaSurveyDB());
        }
    }

    public static void recursiveRemover(ValueDB valueDB, OptionDB option, QuestionDB question,
            SurveyDB surveyDB) {
        if (!valueDB.getOptionDB().equals(option) && question.hasChildren()) {
            surveyDB.removeChildrenValuesFromQuestionRecursively(question, false);
        }
    }

    public static List<QuestionDB> getCompulsoryNotAnsweredQuestions(QuestionDB question) {
        List<QuestionDB> questions = new ArrayList<>();
        if (question.getHeaderDB().getTabDB().getType().equals(Constants.TAB_MULTI_QUESTION)) {
            questions = question.getQuestionsByTab(question.getHeaderDB().getTabDB());
        } else {
            questions.add(question);
        }
        return questions;
    }

    public static int getNumRequired(int numRequired, QuestionDB localQuestion) {
        while (localQuestion.getSibling() != null) {
            if (localQuestion.isCompulsory()) {
                numRequired++;
            }
            localQuestion = localQuestion.getSibling();
        }
        if (!localQuestion.isCompulsory()) {
            numRequired--;
        }
        return numRequired;

    }
    public static void setSurveyAsSent(SurveyDB surveyDB) {
        //Check surveys not in progress
        surveyDB.setStatus(SURVEY_SENT);
        surveyDB.save();
    }

    public void removeSurveysInSession() {
        Session.setMalariaSurveyDB(null);
    }
}
