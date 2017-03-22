package org.eyeseetea.malariacare.domain.usecase;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.SurveyAnsweredRatioCache;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;

public abstract class AReloadSurveyAnsweredRatioUseCase {

    protected Survey survey;

    public AReloadSurveyAnsweredRatioUseCase(Survey survey) {
        this.survey = survey;
    }

    public abstract void execute();


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
