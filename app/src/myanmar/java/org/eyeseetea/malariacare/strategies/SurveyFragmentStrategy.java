package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurveyDB;
import static org.eyeseetea.malariacare.domain.entity.TreatmentQueries.isStockQuestion;
import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragmentStrategy {

    public SurveyDB getRenderSurvey(QuestionDB screenQuestionDB) {
        return (isStockQuestion(screenQuestionDB) || TreatmentQueries.isDynamicStockQuestion(
                screenQuestionDB.getUid()))
                ? Session.getStockSurveyDB()
                : getMalariaSurveyDB();
    }


    public boolean isStockSurvey(SurveyDB survey) {
        return survey.getProgramDB().getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.stockProgramUID));
    }

    public String getMalariaProgram() {
        return ProgramDB.findByUID(PreferencesState.getInstance().getContext().getString(
                R.string.malariaProgramUID)).getUid();
    }


    public static void setSurveyAsSent(SurveyDB survey) {
        //Check surveys not in progress
        survey.setStatus(SURVEY_SENT);
        survey.save();
        setStockSurveyAsSent(survey);
    }

    public static void setStockSurveyAsSent(SurveyDB survey) {
        SurveyDB stockSurvey = TreatmentQueries.getStockSurveyWithEventDate(
                survey.getEventDate());
        if (stockSurvey != null) {
            stockSurvey.setStatus(SURVEY_SENT);
            stockSurvey.save();
        }
    }


    public void removeSurveysInSession() {
        Session.setMalariaSurveyDB(null);
        Session.setStockSurveyDB(null);
    }

    public static SurveyDB getValueBySession() {
        //return  (!isStockQuestion(this)) ? Session.getMalariaSurvey() : Session.getStockSurvey();
        return null;
    }

    public static SurveyDB getValueBySessionWithConditions(QuestionDB questionDB) {
        if (isStockQuestion(questionDB) || TreatmentQueries.isPq(questionDB.getUid())
                || TreatmentQueries.isACT(questionDB.getUid())) {
            return Session.getStockSurveyDB();
        }
        return Session.getMalariaSurveyDB();
    }

    public static SurveyDB getSessionSurveyByStock(QuestionDB questionDB) {
        return (TreatmentQueries.isStockQuestion(questionDB) ? Session.getStockSurveyDB()
                : Session.getMalariaSurveyDB());
    }

    public static void saveValueDDlExtraOperations(ValueDB value, OptionDB option, String uid) {
        if (value != null && TreatmentQueries.isTreatmentQuestion(uid) && TreatmentQueries.isACT(
                uid)
                && !option.getId_option().equals(value.getId_option())) {
            List<SurveyDB> surveys = new ArrayList<>();
            surveys.add(Session.getStockSurveyDB());
            surveys.add(Session.getMalariaSurveyDB());
            for (SurveyDB survey : surveys) {
                deleteStockValues(survey);
            }
        }
    }

    public static SurveyDB getSaveValuesDDLSurvey(QuestionDB questionDB) {
        return ((isStockQuestion(questionDB) || TreatmentQueries.isPq(questionDB.getUid())
                || TreatmentQueries.isACT(questionDB.getUid()))
                ? Session.getStockSurveyDB()
                : Session.getMalariaSurveyDB());
    }

    public static void saveValuesText(ValueDB value, String answer, QuestionDB questionDB,
            SurveyDB survey) {
        if ((TreatmentQueries.isTreatmentQuestion(questionDB.getUid()) || TreatmentQueries.isPq(
                questionDB.getUid())
                || TreatmentQueries.isACT(questionDB.getUid())) && value != null
                && !value.getValue().equals(answer)) {
            List<SurveyDB> surveys = new ArrayList<>();
            surveys.add(Session.getStockSurveyDB());
            surveys.add(Session.getMalariaSurveyDB());
            for (SurveyDB surveyToClean : surveys) {
                deleteStockValues(surveyToClean);
            }
        }
        if (isStockQuestion(questionDB) && value != null && answer.equals("-1")) {
            questionDB.deleteValues(value);
        } else {
            questionDB.createOrSaveValue(answer, value, survey);
            for (QuestionDB propagateQuestionDB : questionDB.getPropagationQuestions()) {
                propagateQuestionDB.createOrSaveValue(answer,
                        ValueDB.findValueFromDatabase(propagateQuestionDB.getId_question(),
                                Session.getMalariaSurveyDB()), Session.getMalariaSurveyDB());
            }
        }
    }

    public static void recursiveRemover(ValueDB value, OptionDB option, QuestionDB questionDB,
            SurveyDB survey) {
        if (!value.getOptionDB().equals(option) && questionDB.hasChildren()
                && !TreatmentQueries.isDynamicTreatmentQuestion(questionDB.getUid())) {
            survey.removeChildrenValuesFromQuestionRecursively(questionDB, false);
        }
    }

    public static List<QuestionDB> getCompulsoryNotAnsweredQuestions(QuestionDB questionDB) {
        List<QuestionDB> questionDBs = new ArrayList<>();
        if (questionDB.getHeaderDB().getTabDB().getType().equals(Constants.TAB_MULTI_QUESTION)) {
            questionDBs = questionDB.getQuestionsByTab(questionDB.getHeaderDB().getTabDB());
        } else if (TreatmentQueries.isDynamicStockQuestion(questionDB.getUid())) {
            List<OptionDB> options = questionDB.getAnswerDB().getOptionDBs();
            for (OptionDB option : options) {
                QuestionDB questionDBNotAnswered = questionDB.findByID(option.getId_option());
                if (!questionDB.isNotAnswered(questionDBNotAnswered)) {
                    questionDBs.add(questionDBNotAnswered);
                }
            }
        } else {
            questionDBs.add(questionDB);
        }
        return questionDBs;
    }

    public static int getNumRequired(int numRequired, QuestionDB localQuestionDB) {
        while (localQuestionDB.getSibling() != null) {
            if (localQuestionDB.isCompulsory() && !isStockQuestion(localQuestionDB)) {
                numRequired++;
            }
            localQuestionDB = localQuestionDB.getSibling();
        }
        if (isStockQuestion(localQuestionDB) || !localQuestionDB.isCompulsory()) {
            numRequired--;
        }
        return numRequired;

    }

    public static void deleteStockValues(SurveyDB survey) {
        List<ValueDB> values = survey.getValuesFromDB();
        for (ValueDB value : values) {
            if (value.getQuestionDB() == null) {
                continue;
            }
            if ((TreatmentQueries.isACT(value.getQuestionDB().getUid()) && !value.getValue().equals(
                    "0"))
                    || TreatmentQueries.isOutStockQuestion(value.getQuestionDB().getUid())) {
                for (QuestionDB questionDBPropagated : value.getQuestionDB().getPropagationQuestions()) {
                    survey.removeValue(
                            questionDBPropagated.getValueBySurvey(Session.getMalariaSurveyDB()));
                }
                value.delete();
            }
        }
    }

    public static SurveyDB getSessionSurveyByQuestion(QuestionDB questionDB) {
        return (TreatmentQueries.isStockQuestion(questionDB)) ? Session.getStockSurveyDB()
                : Session.getMalariaSurveyDB();
    }
}
