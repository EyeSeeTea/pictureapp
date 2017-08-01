package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class ValueLocalDataSource implements IValueRepository {
    @Override
    public List<Value> getValuesFromSurvey(Long idSurvey) {
        return getValuesFromDBValues(getDBOrderValues(idSurvey));

    }

    private List<Value> getValuesFromDBValues(
            List<org.eyeseetea.malariacare.data.database.model.Value> dbOrderValues) {
        List<Value> values = new ArrayList<>();
        for (org.eyeseetea.malariacare.data.database.model.Value dBValue : dbOrderValues) {
            Value value = new Value(dBValue.getValue());
            if (dBValue.getQuestion() != null) {
                value.setQuestionUId(dBValue.getQuestion().getUid());
            }
            if (dBValue.getOption() != null) {
                value.setInternationalizedCode(
                        dBValue.getOption().getInternationalizedCode());
                value.setOptionCode(dBValue.getOption().getCode());
            }
            if (dBValue.getOption() != null && dBValue.getOption().getBackground_colour() != null) {
                String color = "#" + dBValue.getOption().getBackground_colour();
                value.setBackgroundColor(color);
            }
            values.add(value);
        }
        return values;
    }

    private List<org.eyeseetea.malariacare.data.database.model.Value> getDBOrderValues(
            Long idSurvey) {
        Survey survey = Survey.findById(idSurvey);
        List<org.eyeseetea.malariacare.data.database.model.Value> reviewValues = new ArrayList<>();
        List<org.eyeseetea.malariacare.data.database.model.Value> allValues =
                survey.getValuesFromDB();

        for (org.eyeseetea.malariacare.data.database.model.Value value : allValues) {
            boolean isReviewValue = true;
            if (value.getQuestion() == null) {
                continue;
            }
            for (QuestionRelation questionRelation : value.getQuestion().getQuestionRelations()) {
                if (questionRelation.isACounter() || questionRelation.isAReminder()
                        || questionRelation.isAWarning() || questionRelation.isAMatch()) {
                    isReviewValue = false;
                }
            }
            int output = value.getQuestion().getOutput();
            if (output == Constants.HIDDEN
                    || output == Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON) {
                isReviewValue = false;
            }
            if (isReviewValue) {
                if (value.getQuestion() != null) {
                    reviewValues.add(value);
                }
            }
        }
        return reviewValues;
    }


}
