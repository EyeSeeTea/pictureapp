package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class CompletionSurveyUseCase extends ACompletionSurveyUseCase {
    private Survey mSurvey;

    @Override
    public void execute(long idSurvey) {
        Survey survey = getSurveyWithStatusAndAnsweredRatio(idSurvey);
        updateRDTStockQuestion(survey);
    }

    private Survey getSurveyWithStatusAndAnsweredRatio(long idSurvey) {
        org.eyeseetea.malariacare.database.model.Survey surveyDB = org.eyeseetea.malariacare
                .database.model.Survey.findById(idSurvey);
        Survey survey = new Survey(idSurvey);
        survey.setSurveyAnsweredRatio(surveyDB.reloadSurveyAnsweredRatio());
        surveyDB.updateSurveyStatus();
        survey.setStatus(surveyDB.getStatus());
        return survey;
    }

    private void updateRDTStockQuestion(Survey survey) {
        if (survey.getStatus() == Constants.SURVEY_COMPLETED
                || survey.getStatus() == Constants.SURVEY_SENT) {
            org.eyeseetea.malariacare.database.model.Survey surveyDBMalaria = org.eyeseetea.malariacare
                    .database.model.Survey.findById(survey.getId());
            List<Value> surveyValues = surveyDBMalaria.getValuesFromDB();

            org.eyeseetea.malariacare.database.model.Survey surveyDBStock=org.eyeseetea.malariacare
                    .database.model.Survey.getLastSurveyWithType(Constants.SURVEY_EXPENSE);

            Value rdtValue = new Value("1", Question.getRDTQuestion(), surveyDBStock);


            if (isInvalidRDTInValues(surveyValues)) {
                rdtValue.setValue("2");
            } else {
                rdtValue.setValue("1");
            }
            rdtValue.save();
        }
    }

    private boolean isInvalidRDTInValues(List<Value> surveyValues) {
        for (Value value : surveyValues) {
            if (value.getQuestion().isInvalidRDTQuestion()) {
                return true;
            }
        }
        return false;
    }

}
