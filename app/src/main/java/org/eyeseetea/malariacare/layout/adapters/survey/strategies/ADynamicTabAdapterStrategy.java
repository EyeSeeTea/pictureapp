package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import android.os.Handler;
import android.view.View;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
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

    public abstract List<QuestionDB> addAdditionalQuestions(int tabType, List<QuestionDB>
            screenQuestionDBs);

    public abstract void instanceOfSingleQuestion(IQuestionView questionView, QuestionDB screenQuestionDB);

    public abstract void instanceOfMultiQuestion(IQuestionView questionView, QuestionDB screenQuestionDB);

    public abstract void renderParticularSurvey(QuestionDB screenQuestionDB, SurveyDB surveyDB, IQuestionView questionView);

    public abstract boolean isMultiQuestionByVariant(int tabType);

    public abstract void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView);

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                QuestionDB questionDB = mDynamicTabAdapter.navigationController.getCurrentQuestion();
                ValueDB valueDB = questionDB.getValueBySession();
                if (mDynamicTabAdapter.isDone(valueDB)) {
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
