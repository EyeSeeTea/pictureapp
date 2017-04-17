package org.eyeseetea.malariacare.domain.usecase;

import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.SurveyAnsweredRatioCache;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.domain.entity.Treatment;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

import java.util.List;

public class ReloadSurveyAnsweredRatioUseCase extends AReloadSurveyAnsweredRatioUseCase {
    public ReloadSurveyAnsweredRatioUseCase(Survey survey) {
        super(survey);
    }

    @Override
    public void execute() {
        SurveyFragmentStrategy surveyFragmentStrategy = new SurveyFragmentStrategy();
        if (!surveyFragmentStrategy.isStockSurvey(survey)) {
            reloadMalariaSurveyAnsweredRatio();
        } else {
            reloadStockSurveyAnsweredRatio();
        }
    }


    private void reloadStockSurveyAnsweredRatio() {
        int numRequired = 0, numAnswered = 0;
        boolean checkACTAlternative = false, checkPQAlternative = false;
        SurveyAnsweredRatio surveyAnsweredRatio;
        org.eyeseetea.malariacare.data.database.model.Survey malariaSurvey =
                survey.getSurveyWithEventDateProgram(survey.getEventDate(),
                        PreferencesState.getInstance().getContext().getString(
                                R.string.malariaProgramUID));
        List<Value> stockValues = survey.getValuesFromDB();
        List<Value> malariaValues = malariaSurvey.getValuesFromDB();
        org.eyeseetea.malariacare.domain.entity.Treatment treatment = new Treatment(malariaSurvey,
                survey);
        treatment.hasTreatment();
        List<Question> mainTreatmentQuestions = treatment.getQuestions();
        for (Question question : mainTreatmentQuestions) {
            if (question.isCompulsory()) {
                numRequired++;
                if (TreatmentQueries.isStockQuestion(question)) {
                    for (Value value : stockValues) {
                        if (value.getQuestion().getUid().equals(question.getUid())) {
                            if (value.getValue() != null) numAnswered++;
                            if (value.getValue().equals("0")) {
                                if (TreatmentQueries.isACT(question.getUid())) {
                                    numRequired++;
                                    checkACTAlternative = true;
                                } else if (TreatmentQueries.isPq(question.getUid())) {
                                    numRequired++;
                                    checkPQAlternative = true;
                                }
                            }
                        }
                    }
                } else {
                    for (Value value : malariaValues) {
                        if (value.getQuestion().getUid().equals(question.getUid())
                                && value.getValue() != null) {
                            numAnswered++;
                        }
                    }
                }
            }
        }
        if (checkACTAlternative) {
            numAnswered += checkACTAlternatives(treatment, stockValues, malariaValues);
        }
        if (checkPQAlternative) {
            numAnswered += checkPqAlternatives(malariaValues);
        }
        surveyAnsweredRatio = new SurveyAnsweredRatio(numRequired, numAnswered);

        SurveyAnsweredRatioCache.put(survey.getId_survey(), surveyAnsweredRatio);
        survey.setAnsweredQuestionRatio(surveyAnsweredRatio);

    }

    private int checkACTAlternatives(Treatment treatment, List<Value> stockValues,
            List<Value> malariaValues) {
        int numAnswered = 0;
        boolean checkOutStockQuestion = true;
        for (Question question : treatment.getACTAlternativeStockQuestions()) {
            for (Value value : stockValues) {
                if (value.getValue() != null && value.getQuestion().getUid().equals(
                        question.getUid()) && !value.getValue().equals("0")) {
                    checkOutStockQuestion = false;
                    numAnswered++;
                }
            }
        }
        if (checkOutStockQuestion) {
            for (Value value : malariaValues) {
                if (value.getQuestion().getUid().equals(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.outOfStockQuestionUID)) && value.getValue() != null) {
                    numAnswered++;
                }
            }
        }
        return numAnswered;
    }

    private int checkPqAlternatives(List<Value> malariaValues) {
        int numAnswered = 0;
        for (Value value : malariaValues) {
            if (value.getQuestion().equals(Question.findByUID(
                    PreferencesState.getInstance().getContext().getResources().getString(
                            R.string.alternativePqQuestionUID)))
                    && value.getValue() != null) {
                numAnswered++;
            }
        }
        return numAnswered;
    }

    @Override
    protected void reloadMalariaSurveyAnsweredRatio() {
        SurveyAnsweredRatio surveyAnsweredRatio;
        //First parent is always required and not calculated.
        int numRequired = 1;
        int numAnswered = 0;

        Program program = Program.getFirstProgram();
        Tab tab = program.getTabs().get(0);
        Question rootQuestion = Question.findRootQuestion(tab);
        Question localQuestion = rootQuestion;
        while (localQuestion.getSibling() != null) {
            if (localQuestion.isCompulsory() && !TreatmentQueries.isStockQuestion(localQuestion)) {
                numRequired++;
            }
            localQuestion = localQuestion.getSibling();
        }
        if (TreatmentQueries.isStockQuestion(localQuestion) || !localQuestion.isCompulsory()) {
            numRequired--;
        }

        //Add children required by each parent (value+question)
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion().isCompulsory() && value.getId_option() != null) {
                numRequired += Question.countChildrenByOptionValue(value.getId_option());
            }
        }
        numAnswered += org.eyeseetea.malariacare.data.database.model.Survey.countCompulsoryBySurvey(
                survey);
        Log.d("survey answered", "num required: " + numRequired + " num answered: " + numAnswered);
        surveyAnsweredRatio = new SurveyAnsweredRatio(numRequired, numAnswered);

        SurveyAnsweredRatioCache.put(survey.getId_survey(), surveyAnsweredRatio);
        survey.setAnsweredQuestionRatio(surveyAnsweredRatio);
    }
}
