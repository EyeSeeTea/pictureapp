package org.eyeseetea.malariacare.domain.usecase;

import android.support.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;
import java.util.Map;

public class CompletionSurveyUseCase extends ACompletionSurveyUseCase {
    Function<Value, QuestionDB> valuesToQuestions = new Function<Value, QuestionDB>() {
        @Nullable
        @Override
        public QuestionDB apply(Value value) {
            return value.getQuestionDB();
        }
    };
    private Survey mSurvey;

    @Override
    public void execute(long idSurvey) {
        Survey survey = getSurveyWithStatusAndAnsweredRatio(idSurvey);
        updateRDTStockQuestion(survey);
    }

    private Survey getSurveyWithStatusAndAnsweredRatio(long idSurvey) {
        org.eyeseetea.malariacare.data.database.model.SurveyDB surveyDB =
                org.eyeseetea.malariacare.data
                        .database.model.SurveyDB.findById(idSurvey);
        Survey survey = new Survey(idSurvey);
        survey.setSurveyAnsweredRatio(surveyDB.reloadSurveyAnsweredRatio());
        surveyDB.updateSurveyStatus();
        survey.setStatus(surveyDB.getStatus());
        return survey;
    }

    private void updateRDTStockQuestion(Survey survey) {
        if (survey.getStatus() == Constants.SURVEY_COMPLETED
                || survey.getStatus() == Constants.SURVEY_SENT) {
            org.eyeseetea.malariacare.data.database.model.SurveyDB surveyDBMalaria =
                    org.eyeseetea.malariacare.data
                            .database.model.SurveyDB.findById(survey.getId());
            List<Value> surveyValues = surveyDBMalaria.getValuesFromDB();

            org.eyeseetea.malariacare.data.database.model.SurveyDB surveyDBStock =
                    org.eyeseetea.malariacare.data
                            .database.model.SurveyDB.getLastSurveyWithType(Constants.SURVEY_ISSUE);

            Value rdtStockValue = TreatmentQueries.getStockRDTQuestion().insertValue("1",
                    surveyDBStock);

            rdtStockValue.setValue(Integer.toString(rdtUsed(surveyValues)));

            rdtStockValue.save();

            for (QuestionDB propagateQuestionDB : TreatmentQueries.getStockRDTQuestion()
                    .getPropagationQuestions()) {
                propagateQuestionDB.insertValue(rdtStockValue.getValue(),
                        Session.getMalariaSurveyDB()).save();
            }
        }
    }

    private int rdtUsed(List<Value> surveyValues) {
        int rdtUsed = 1;
        Map<QuestionDB, Value> answersMap = Maps.uniqueIndex(surveyValues, valuesToQuestions);
        QuestionDB rdtQuestionDB = TreatmentQueries.getRDTQuestion();
        QuestionDB confirmInvalid = TreatmentQueries.getInvalidCounterQuestion();
        if (answersMap.keySet().contains(confirmInvalid)) {
            int invalids;
            try {
                invalids = Integer.parseInt(answersMap.get(confirmInvalid).getValue());
            } catch (NumberFormatException exception) {
                invalids = 1;
            }
            if (answersMap.get(rdtQuestionDB).getValue().equals("Invalid")) {
                rdtUsed = invalids;
            } else {
                rdtUsed += invalids;
            }
        }
        return rdtUsed;
    }
}
