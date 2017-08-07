package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import android.os.Handler;
import android.view.View;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import android.view.View;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.strategies.UIMessagesStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class DynamicTabAdapterStrategy extends ADynamicTabAdapterStrategy {

    public DynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        super(dynamicTabAdapter);
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
    public List<QuestionDB> addAdditionalQuestions(int tabType, List<QuestionDB> screenQuestions) {
        return null;
    }


    @Override
    public void instanceOfSingleQuestion(IQuestionView questionView, QuestionDB screenQuestion) {
        return;
    }

    @Override
    public void instanceOfMultiQuestion(IQuestionView questionView, QuestionDB screenQuestion) {
        return;
    }

    @Override
    public void renderParticularSurvey(QuestionDB screenQuestion, SurveyDB survey,
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
    public void addScrollToSwipeTouchListener(View rowView) {
    }



    @Override
    public void finishOrNext() {
        try {
            System.out.println(Session.getMalariaSurveyDB().getValuesFromDB().toString());
            System.out.println(Session.getStockSurveyDB().getValuesFromDB().toString());
        } catch (Exception e) {
        }
        if (Validation.hasErrors()) {
            Validation.showErrors();
            DynamicTabAdapter.setIsClicked(false);
            return;
        }
        if (mDynamicTabAdapter.navigationController.getCurrentQuestion().hasCompulsoryNotAnswered
                ()) {

            UIMessagesStrategy.getInstance().showCompulsoryUnansweredToast();
            DynamicTabAdapter.setIsClicked(false);
            return;
        }
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                mDynamicTabAdapter.navigationController.isMovingToForward = false;
                if (!shouldShowReviewScreen() || !BuildConfig.reviewScreen) {
                    mDynamicTabAdapter.surveyShowDone();
                } else {
                    DashboardActivity.dashboardActivity.showReviewFragment();
                    mDynamicTabAdapter.hideKeyboard(
                            PreferencesState.getInstance().getContext());
                    DynamicTabAdapter.setIsClicked(false);
                }
                return;
            }
        }, 750);
    }
}
