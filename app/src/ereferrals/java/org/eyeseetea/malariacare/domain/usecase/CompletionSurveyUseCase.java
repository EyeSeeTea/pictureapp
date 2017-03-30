package org.eyeseetea.malariacare.domain.usecase;

import android.support.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;
import java.util.Map;

public class CompletionSurveyUseCase extends ACompletionSurveyUseCase {
    Function<Value, Question> valuesToQuestions = new Function<Value, Question>() {
        @Nullable
        @Override
        public Question apply(Value value) {
            return value.getQuestion();
        }
    };
    private Survey mSurvey;

    @Override
    public void execute(long idSurvey) {
        Survey survey = getSurveyWithStatusAndAnsweredRatio(idSurvey);
        updateRDTStockQuestion(survey);
    }

    private Survey getSurveyWithStatusAndAnsweredRatio(long idSurvey) {
        org.eyeseetea.malariacare.data.database.model.Survey surveyDB =
                org.eyeseetea.malariacare.data
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
            org.eyeseetea.malariacare.data.database.model.Survey surveyDBMalaria =
                    org.eyeseetea.malariacare.data
                            .database.model.Survey.findById(survey.getId());
            List<Value> surveyValues = surveyDBMalaria.getValuesFromDB();

            org.eyeseetea.malariacare.data.database.model.Survey surveyDBStock =
                    org.eyeseetea.malariacare.data
                            .database.model.Survey.getLastSurveyWithType(Constants.SURVEY_ISSUE);

            Value rdtStockValue = Question.getStockRDTQuestion().insertValue("1",
                    surveyDBStock);

            rdtStockValue.setValue(Integer.toString(rdtUsed(surveyValues)));

            rdtStockValue.save();

            for (Question propagateQuestion : Question.getStockRDTQuestion()
                    .getPropagationQuestions()) {
                propagateQuestion.insertValue(rdtStockValue.getValue(),
                        Session.getMalariaSurvey()).save();
            }
        }
    }

    private int rdtUsed(List<Value> surveyValues) {
        int rdtUsed = 1;
        Map<Question, Value> answersMap = Maps.uniqueIndex(surveyValues, valuesToQuestions);
        Question rdtQuestion = Question.getRDTQuestion();
        Question confirmInvalid = Question.getInvalidCounterQuestion();
        if (answersMap.keySet().contains(confirmInvalid)) {
            int invalids;
            try {
                invalids = Integer.parseInt(answersMap.get(confirmInvalid).getValue());
            } catch (NumberFormatException exception) {
                invalids = 1;
            }
            if (answersMap.get(rdtQuestion).getValue().equals("Invalid")) {
                rdtUsed = invalids;
            } else {
                rdtUsed += invalids;
            }
        }
        return rdtUsed;
    }
}
