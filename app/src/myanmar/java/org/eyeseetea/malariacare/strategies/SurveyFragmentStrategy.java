package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;
import static org.eyeseetea.malariacare.domain.entity.TreatmentQueries.isStockQuestion;
import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragmentStrategy {

    public Survey getRenderSurvey(Question screenQuestion) {
        return (isStockQuestion(screenQuestion) || TreatmentQueries.isDynamicStockQuestion(
                screenQuestion.getUid()))
                ? Session.getStockSurvey()
                : getMalariaSurvey();
    }


    public boolean isStockSurvey(Survey survey) {
        return survey.getProgram().getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.stockProgramUID));
    }

    public String getMalariaProgram() {
        return Program.findByUID(PreferencesState.getInstance().getContext().getString(
                R.string.malariaProgramUID)).getUid();
    }


    public static void setSurveyAsSent(Survey survey) {
        //Check surveys not in progress
        survey.setStatus(SURVEY_SENT);
        survey.save();
        setStockSurveyAsSent(survey);
    }

    public static void setStockSurveyAsSent(Survey survey) {
        Survey stockSurvey = TreatmentQueries.getStockSurveyWithEventDate(
                survey.getEventDate());
        if (stockSurvey != null) {
            stockSurvey.setStatus(SURVEY_SENT);
            stockSurvey.save();
        }
    }


    public void removeSurveysInSession() {
        Session.setMalariaSurvey(null);
        Session.setStockSurvey(null);
    }

    public static Survey getValueBySession() {
        //return  (!isStockQuestion(this)) ? Session.getMalariaSurvey() : Session.getStockSurvey();
        return null;
    }

    public static Survey getValueBySessionWithConditions(Question question) {
        if (isStockQuestion(question) || TreatmentQueries.isPq(question.getUid())
                || TreatmentQueries.isACT(question.getUid())) {
            return Session.getStockSurvey();
        }
        return Session.getMalariaSurvey();
    }

    public static Survey getSessionSurveyByStock(Question question) {
        return (TreatmentQueries.isStockQuestion(question) ? Session.getStockSurvey()
                : Session.getMalariaSurvey());
    }

    public static void saveValueDDlExtraOperations(Value value, Option option, String uid) {
        if (value != null && TreatmentQueries.isTreatmentQuestion(uid) && TreatmentQueries.isACT(
                uid)
                && !option.getId_option().equals(value.getId_option())) {
            List<Survey> surveys = new ArrayList<>();
            surveys.add(Session.getStockSurvey());
            surveys.add(Session.getMalariaSurvey());
            for (Survey survey : surveys) {
                deleteStockValues(survey);
            }
        }
    }

    public static Survey getSaveValuesDDLSurvey(Question question) {
        return ((isStockQuestion(question) || TreatmentQueries.isPq(question.getUid())
                || TreatmentQueries.isACT(question.getUid()))
                ? Session.getStockSurvey()
                : Session.getMalariaSurvey());
    }

    public static void saveValuesText(Value value, String answer, Question question,
            Survey survey) {
        if ((TreatmentQueries.isTreatmentQuestion(question.getUid()) || TreatmentQueries.isPq(
                question.getUid())
                || TreatmentQueries.isACT(question.getUid())) && value != null
                && !value.getValue().equals(answer)) {
            List<Survey> surveys = new ArrayList<>();
            surveys.add(Session.getStockSurvey());
            surveys.add(Session.getMalariaSurvey());
            for (Survey surveyToClean : surveys) {
                deleteStockValues(surveyToClean);
            }
        }
        if (isStockQuestion(question) && value != null && answer.equals("-1")) {
            question.deleteValues(value);
        } else {
            question.createOrSaveValue(answer, value, survey);
            for (Question propagateQuestion : question.getPropagationQuestions()) {
                propagateQuestion.createOrSaveValue(answer,
                        Value.findValueFromDatabase(propagateQuestion.getId_question(),
                                Session.getMalariaSurvey()), Session.getMalariaSurvey());
            }
        }
    }

    public static void recursiveRemover(Value value, Option option, Question question,
            Survey survey) {
        if (!value.getOption().equals(option) && question.hasChildren()
                && !TreatmentQueries.isDynamicTreatmentQuestion(question.getUid())) {
            survey.removeChildrenValuesFromQuestionRecursively(question, false);
        }
    }

    public static List<Question> getCompulsoryNotAnsweredQuestions(Question question) {
        List<Question> questions = new ArrayList<>();
        if (question.getHeader().getTab().getType().equals(Constants.TAB_MULTI_QUESTION)) {
            questions = question.getQuestionsByTab(question.getHeader().getTab());
        } else if (TreatmentQueries.isDynamicStockQuestion(question.getUid())) {
            List<Option> options = question.getAnswer().getOptions();
            for (Option option : options) {
                Question questionNotAnswered = question.findByID(option.getId_option());
                if (!question.isNotAnswered(questionNotAnswered)) {
                    questions.add(questionNotAnswered);
                }
            }
        } else {
            questions.add(question);
        }
        return questions;
    }

    public static int getNumRequired(int numRequired, Question localQuestion) {
        while (localQuestion.getSibling() != null) {
            if (localQuestion.isCompulsory() && !isStockQuestion(localQuestion)) {
                numRequired++;
            }
            localQuestion = localQuestion.getSibling();
        }
        if (isStockQuestion(localQuestion) || !localQuestion.isCompulsory()) {
            numRequired--;
        }
        return numRequired;

    }

    public static void deleteStockValues(Survey survey) {
        List<Value> values = survey.getValuesFromDB();
        for (Value value : values) {
            if (value.getQuestion() == null) {
                continue;
            }
            if ((TreatmentQueries.isACT(value.getQuestion().getUid()) && !value.getValue().equals(
                    "0"))
                    || TreatmentQueries.isOutStockQuestion(value.getQuestion().getUid())) {
                for (Question questionPropagated : value.getQuestion().getPropagationQuestions()) {
                    survey.removeValue(
                            questionPropagated.getValueBySurvey(Session.getMalariaSurvey()));
                }
                value.delete();
            }
        }
    }

    public static Survey getSessionSurveyByQuestion(Question question) {
        return (TreatmentQueries.isStockQuestion(question)) ? Session.getStockSurvey()
                : Session.getMalariaSurvey();
    }
}
