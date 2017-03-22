package org.eyeseetea.malariacare.layout.adapters.survey.strategies;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class DynamicTabAdapterStrategy implements IDynamicTabAdapterStrategy {

    DynamicTabAdapter mDynamicTabAdapter;

    public DynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    @Override
    public boolean HasQuestionImageVisibleInHeader(Integer output) {
        return output != Constants.SWITCH_BUTTON && output != Constants.QUESTION_LABEL
                && output != Constants.RADIO_GROUP_HORIZONTAL && output != Constants.REMINDER
                && output != Constants.WARNING;
    }
    @Override
    public void initSurveys(boolean readOnly) {
        return;
    }

    @Override
    public List<Question> addAdditionalQuestions(int tabType, List<Question> screenQuestions) {
        return null;
    }

    @Override
    public void instanceOfSingleQuestion(IQuestionView questionView, Question screenQuestion) {
        return;
    }

    @Override
    public void instanceOfMultiQuestion(IQuestionView questionView, Question screenQuestion) {
        return;
    }

    @Override
    public void renderParticularSurvey(Question screenQuestion, Survey survey,
            IQuestionView questionView) {
        return;
    }

    @Override
    public boolean isMultiQuestionByVariant(int tabType) {
        return false;
    }

    @Override
    public void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView) {
        return;
    }
}
