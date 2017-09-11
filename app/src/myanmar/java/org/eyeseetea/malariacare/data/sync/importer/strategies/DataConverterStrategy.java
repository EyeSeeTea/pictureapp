package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.QuestionNotFoundException;

import java.util.List;

public class DataConverterStrategy implements IDataConverterStrategy {

    public static final int TRUE_POSITION = 1;
    private static String ORG_UNIT_QUESTION_UID = "NoDataElementOrgUnit";
    Context mContext;


    public DataConverterStrategy(Context context) {
        mContext = context;
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

    public void convert(ConvertFromSDKVisitor converter,
            EventExtended event) throws QuestionNotFoundException {

        if (!event.getProgramUId().equals(mContext.getString(R.string.stockProgramUID))) {
            convertOrgUnitDataValue(converter, event);

            convertNoDataElementMatchQuestions(converter, event);
        }
    }

    private void convertOrgUnitDataValue(ConvertFromSDKVisitor converter,
            EventExtended event) throws QuestionNotFoundException {

        Question orgUnitQuestion = Question.findByUID(ORG_UNIT_QUESTION_UID);

        if (orgUnitQuestion == null) {
            throw new QuestionNotFoundException(
                    String.format("Question with uid %s not found", ORG_UNIT_QUESTION_UID));
        }

        createDataValueExtended(event, orgUnitQuestion.getUid(), event.getOrganisationUnitId(),
                converter);
        DataValueExtended OrgUnitDataValue = new DataValueExtended();
        OrgUnitDataValue.setEvent(event.getEvent());
        OrgUnitDataValue.setDataElement(orgUnitQuestion.getUid());
        OrgUnitDataValue.setValue(event.getOrganisationUnitId());
        OrgUnitDataValue.accept(converter);

    }

    private void convertNoDataElementMatchQuestions(ConvertFromSDKVisitor converter,
            EventExtended event) {
        List<Question> questionsWithMatch = Question.getAllQuestionsWithMatch();

        List<DataValueExtended> dataValues = DataValueExtended.getExtendedList(
                SdkQueries.getDataValues(event.getUid()));

        for (Question question : questionsWithMatch) {
            createValueForQuestion(question, event, dataValues, converter);
        }
    }

    private void createValueForQuestion(Question question, EventExtended event,
            List<DataValueExtended> dataValues, ConvertFromSDKVisitor converter) {

        List<QuestionOption> matchedQuestionOptions = question.getQuestionOptionsOfTypeMatch();

        List<Option> optionsWithoutMatch = question.getAnswer().getOptions();

        Option selectedOption = null;

        for (QuestionOption matchedQuestionOption : matchedQuestionOptions) {
            Question matchedQuestion =
                    matchedQuestionOption.getMatch().getQuestionRelation().getQuestion();

            Option matchedOption = matchedQuestionOption.getOption();

            if (optionsWithoutMatch.contains(matchedOption)) {
                optionsWithoutMatch.remove(matchedOption);
            }

            DataValueExtended valueFromServer = getValueFromServer(dataValues,
                    matchedQuestion.getUid());

            if (valueFromServer != null) {
                Option trueHiddenOption = matchedQuestion.getAnswer().getOptions().get(
                        TRUE_POSITION);

                Option selectedHiddenOption = matchedQuestion.findOptionByValue(
                        valueFromServer.getValue());

                if (selectedHiddenOption.getCode().equals(trueHiddenOption.getCode())) {
                    selectedOption = matchedOption;
                    break;
                }
            } else {
                Log.e(this.getClass().getSimpleName(),
                        "Value not create for question " + question.getUid());
                return;
            }
        }

        if (selectedOption == null) {
            if (optionsWithoutMatch.size() > 0) {
                selectedOption = optionsWithoutMatch.get(0);
            }
        }

        if (selectedOption != null) {
            createDataValueExtended(event, question.getUid(), selectedOption.getCode(), converter);
        } else {
            Log.e(this.getClass().getSimpleName(),
                    "Value not create for question " + question.getUid());
        }

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
