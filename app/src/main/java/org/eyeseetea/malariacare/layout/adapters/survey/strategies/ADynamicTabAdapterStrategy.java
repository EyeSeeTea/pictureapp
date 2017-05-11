package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import android.os.Handler;
import android.view.View;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.strategies.ReviewFragmentStrategy;
import org.eyeseetea.malariacare.strategies.UIMessagesStrategy;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public abstract class ADynamicTabAdapterStrategy {

    protected DynamicTabAdapter mDynamicTabAdapter;

    public ADynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

   public abstract boolean HasQuestionImageVisibleInHeader(Integer output);

    public abstract void initSurveys(boolean readOnly);

    public abstract List<Question> addAdditionalQuestions(int tabType, List<Question> screenQuestions);

    public abstract void instanceOfSingleQuestion(IQuestionView questionView, Question screenQuestion);

    public abstract void instanceOfMultiQuestion(IQuestionView questionView, Question screenQuestion);

    public abstract void renderParticularSurvey(Question screenQuestion, Survey survey, IQuestionView questionView);

    public abstract boolean isMultiQuestionByVariant(int tabType);

    public abstract void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView);

    public void finishOrNext() {
        try {
            System.out.println(Session.getMalariaSurvey().getValuesFromDB().toString());
            System.out.println(Session.getStockSurvey().getValuesFromDB().toString());
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Question question = mDynamicTabAdapter.navigationController.getCurrentQuestion();
                Value value = question.getValueBySession();
                if (mDynamicTabAdapter.isDone(value)) {
                    mDynamicTabAdapter.navigationController.isMovingToForward = false;
                    if (!ReviewFragmentStrategy.shouldShowReviewScreen() || !BuildConfig.reviewScreen) {
                        mDynamicTabAdapter.surveyShowDone();
                    } else {
                        DashboardActivity.dashboardActivity.showReviewFragment();
                        mDynamicTabAdapter.hideKeyboard(
                                PreferencesState.getInstance().getContext());
                        DynamicTabAdapter.setIsClicked(false);
                    }
                    return;
                }
                mDynamicTabAdapter.next();
            }
        }, 750);
    }
    public abstract void addScrollToSwipeTouchListener(View rowView);
}
