package org.eyeseetea.malariacare.layout.adapters.survey.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.ImageRadioButtonSingleQuestionView;

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
        Session.getMalariaSurvey().getValuesFromDB();
    }
}
