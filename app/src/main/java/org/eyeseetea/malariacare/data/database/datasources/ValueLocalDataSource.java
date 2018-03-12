package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
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

    @Override
    public void saveValue(Value value, long idSurvey) {
        ValueDB valueDB = ValueDB.findValueFromDatabase(
                QuestionDB.findByUID(value.getQuestionUId()).getId_question(),
                SurveyDB.findById(idSurvey));

        if (value.getOptionCode() != null && !value.getOptionCode().isEmpty()) {
            if (valueDB == null) {
                valueDB = new ValueDB(OptionDB.findByCode(value.getOptionCode()),
                        QuestionDB.findByUID(value.getQuestionUId()),
                        SurveyDB.findById(idSurvey));
            } else {
                valueDB.setValue(value.getValue());
                valueDB.setOptionDB(OptionDB.findByCode(value.getOptionCode()));
            }
        } else {
            if (valueDB == null) {
                valueDB = new ValueDB(value.getValue(),
                        QuestionDB.findByUID(value.getQuestionUId()),
                        SurveyDB.findById(idSurvey));
            } else {
                valueDB.setValue(value.getValue());
            }
        }
        valueDB.save();
    }

    private List<Value> getValuesFromDBValues(
            List<org.eyeseetea.malariacare.data.database.model.ValueDB> dbOrderValues) {
        List<Value> values = new ArrayList<>();
        for (org.eyeseetea.malariacare.data.database.model.ValueDB dBValue : dbOrderValues) {
            Value value = new Value(dBValue.getValue());
            if (dBValue.getQuestionDB() != null) {
                value.setQuestionUId(dBValue.getQuestionDB().getUid());
            }
            if (dBValue.getOptionDB() != null) {
                value.setInternationalizedName(
                        dBValue.getOptionDB().getInternationalizedName());
                value.setOptionCode(dBValue.getOptionDB().getCode());
            }
            if (dBValue.getOptionDB() != null && dBValue.getOptionDB().getBackground_colour() != null) {
                String color = "#" + dBValue.getOptionDB().getBackground_colour();
                value.setBackgroundColor(color);
            }
            values.add(value);
        }
        return values;
    }

    private List<org.eyeseetea.malariacare.data.database.model.ValueDB> getDBOrderValues(
            Long idSurvey) {
        SurveyDB survey = SurveyDB.findById(idSurvey);
        List<org.eyeseetea.malariacare.data.database.model.ValueDB> reviewValues = new ArrayList<>();
        List<org.eyeseetea.malariacare.data.database.model.ValueDB> allValues =
                survey.getValuesFromDB();

        for (org.eyeseetea.malariacare.data.database.model.ValueDB value : allValues) {
            boolean isReviewValue = true;
            if (value.getQuestionDB() == null) {
                continue;
            }
            for (QuestionRelationDB questionRelation : value.getQuestionDB().getQuestionRelationDBs()) {
                if (questionRelation.isACounter() || questionRelation.isAReminder()
                        || questionRelation.isAWarning() || questionRelation.isAMatch()) {
                    isReviewValue = false;
                }
            }
            int output = value.getQuestionDB().getOutput();
            if (output == Constants.HIDDEN
                    || output == Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON) {
                isReviewValue = false;
            }
            if (isReviewValue) {
                if (value.getQuestionDB() != null) {
                    reviewValues.add(value);
                }
            }
        }
        return reviewValues;
    }


}
