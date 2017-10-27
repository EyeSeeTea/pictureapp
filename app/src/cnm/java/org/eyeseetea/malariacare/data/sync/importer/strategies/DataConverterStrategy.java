package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.QuestionNotFoundException;

import java.util.List;

public class DataConverterStrategy implements IDataConverterStrategy {
    public static final int TRUE_POSITION = 1;

    public DataConverterStrategy(Context context) {
    }

    @Override
    public void convert(ConvertFromSDKVisitor converter, EventExtended event)
            throws QuestionNotFoundException {
        convertNoDataElementMatchQuestions(converter, event);
    }


    private void convertNoDataElementMatchQuestions(ConvertFromSDKVisitor converter,
            EventExtended event) {
        List<QuestionDB> questionsWithMatch = QuestionDB.getAllQuestionsWithMatch();

        List<DataValueExtended> dataValues = DataValueExtended.getExtendedList(
                SdkQueries.getDataValues(event.getUid()));

        for (QuestionDB questionDB : questionsWithMatch) {
            createValueForQuestion(questionDB, event, dataValues, converter);
        }
    }

    private void createValueForQuestion(QuestionDB questionDB, EventExtended event,
            List<DataValueExtended> dataValues, ConvertFromSDKVisitor converter) {

        List<QuestionOptionDB> matchedQuestionOptions = questionDB.getQuestionOptionsOfTypeMatch();

        List<OptionDB> optionsWithoutMatch = questionDB.getAnswerDB().getOptionDBs();

        OptionDB selectedOption = null;

        for (QuestionOptionDB matchedQuestionOption : matchedQuestionOptions) {
            QuestionDB matchedQuestionDB =
                    matchedQuestionOption.getMatchDB().getQuestionRelationDB().getQuestionDB();

            OptionDB matchedOption = matchedQuestionOption.getOptionDB();

            if (optionsWithoutMatch.contains(matchedOption)) {
                optionsWithoutMatch.remove(matchedOption);
            }

            DataValueExtended valueFromServer = getValueFromServer(dataValues,
                    matchedQuestionDB.getUid());

            if (valueFromServer != null) {
                OptionDB trueHiddenOption = matchedQuestionDB.getAnswerDB().getOptionDBs().get(
                        TRUE_POSITION);

                OptionDB selectedHiddenOption = matchedQuestionDB.findOptionByValue(
                        valueFromServer.getValue());

                if (selectedHiddenOption.getCode().equals(trueHiddenOption.getCode())) {
                    selectedOption = matchedOption;
                    break;
                }
            } else {
                Log.e(this.getClass().getSimpleName(),
                        "ValueDB not create for questionDB " + questionDB.getUid());
                return;
            }
        }

        if (selectedOption == null) {
            if (optionsWithoutMatch.size() > 0) {
                selectedOption = optionsWithoutMatch.get(0);
            }
        }

        if (selectedOption != null) {
            createDataValueExtended(event, questionDB.getUid(), selectedOption.getCode(),
                    converter);
        } else {
            Log.e(this.getClass().getSimpleName(),
                    "ValueDB not create for questionDB " + questionDB.getUid());
        }

    }

    private static DataValueExtended getValueFromServer(List<DataValueExtended> dataValues,
            String uid) {
        DataValueExtended dataValuesExtended = null;

        for (DataValueExtended dataValue : dataValues) {
            if (dataValue.getDataElement().equals(uid)) {
                dataValuesExtended = dataValue;
            }
        }

        return dataValuesExtended;
    }

    private void createDataValueExtended(EventExtended event, String dataElement,
            String value, ConvertFromSDKVisitor converter) {
        DataValueExtended dataValueExtended = new DataValueExtended();
        dataValueExtended.setEvent(event.getEvent());
        dataValueExtended.setDataElement(dataElement);
        dataValueExtended.setValue(value);

        dataValueExtended.accept(converter);
    }
}
