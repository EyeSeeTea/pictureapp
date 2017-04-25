package org.eyeseetea.malariacare.strategies;


import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragmentStrategy extends ASurveyFragmentStrategy {


    public static Survey getSessionSurveyByQuestion(Question question) {
        return Session.getMalariaSurvey();
    }

    public static void saveValueDDlExtraOperations(Value value, Option option, String uid) {

    }

    public static Survey getSaveValuesDDLSurvey(Question question) {
        return Session.getMalariaSurvey();
    }

    public static void saveValuesText(Value value, String answer,
            Question question, Survey survey) {
        question.createOrSaveValue(answer, value, survey);
        for (Question propagateQuestion : question.getPropagationQuestions()) {
            propagateQuestion.createOrSaveValue(answer,
                    Value.findValueFromDatabase(propagateQuestion.getId_question(),
                            Session.getMalariaSurvey()), Session.getMalariaSurvey());
        }
    }

    public static void recursiveRemover(Value value, Option option, Question question,
            Survey survey) {
        if (!value.getOption().equals(option) && question.hasChildren()) {
            survey.removeChildrenValuesFromQuestionRecursively(question, false);
        }
    }

    public static List<Question> getCompulsoryNotAnsweredQuestions(Question question) {
        List<Question> questions = new ArrayList<>();
        if (question.getHeader().getTab().getType().equals(Constants.TAB_MULTI_QUESTION)) {
            questions = question.getQuestionsByTab(question.getHeader().getTab());
        } else {
            questions.add(question);
        }
        return questions;
    }

    public static int getNumRequired(int numRequired, Question localQuestion) {
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

    public static void setSurveyAsSent(Survey survey) {
        //Check surveys not in progress
        survey.setStatus(SURVEY_SENT);
        survey.save();
    }

    public Survey getRenderSurvey(Question screenQuestion) {
        return Session.getMalariaSurvey();
    }

    @Override
    boolean isStockSurvey(Survey survey) {
        return false;
    }

    public String getMalariaProgram() {
        Program program = Program.getFirstProgram();
        if (program == null) {
            return "";
        }
        return Program.getFirstProgram().getUid();
    }

    public void removeSurveysInSession() {
        Session.setMalariaSurvey(null);
    }
}