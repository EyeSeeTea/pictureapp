package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.utils.Constants;

public class DynamicTabAdapterStrategy implements IDynamicTabAdapterStrategy {

    DynamicTabAdapter mDynamicTabAdapter;

    public DynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    @Override
    public boolean HasQuestionImageVisibleInHeader(Integer output) {
        return output != Constants.SWITCH_BUTTON && output != Constants.QUESTION_LABEL
                && output != Constants.RADIO_GROUP_HORIZONTAL && output != Constants.REMINDER
                && output != Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT;
    }

    @Override
    public void initSurveys(boolean readOnly) {
        if (readOnly) {
            Survey malariaSurvey = Session.getMalariaSurvey();
        }
    }

    @Override
    public void OnOptionAnswered(View view, Option selectedOption, boolean moveToNextQuestion) {
        if (moveToNextQuestion) {
            mDynamicTabAdapter.navigationController.isMovingToForward = true;
        }

        Question question = (Question) view.getTag();

        if (!selectedOption.getCode().isEmpty()
                && question.getOutput() == Constants.DROPDOWN_OU_LIST) {
            OrgUnit orgUnit = OrgUnit.findByUID(selectedOption.getCode());

            mDynamicTabAdapter.assignOrgUnitToSurvey(Session.getMalariaSurvey(), orgUnit);
        }


        Question counterQuestion = question.findCounterByOption(selectedOption);
        if (counterQuestion == null) {
            mDynamicTabAdapter.saveOptionValue(view, selectedOption, question, moveToNextQuestion);
        } else if (!(view instanceof ImageRadioButtonSingleQuestionView)) {
            mDynamicTabAdapter.showConfirmCounter(view, selectedOption, question, counterQuestion);
        }
    }

    @Override
    public void initSurveyValues() {
        getMalariaSurvey().getValuesFromDB();
    }
}
