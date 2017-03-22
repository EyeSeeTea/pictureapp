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
            Session.setStockSurvey(
                    Survey.getStockSurveyWithEventDate(malariaSurvey.getEventDate()));
        }
    }
}
