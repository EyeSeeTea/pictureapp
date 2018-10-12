package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurveyDB;

import android.util.Log;
import android.view.View;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.strategies.UIMessagesStrategy;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
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

    public abstract void instanceOfSingleQuestion(IQuestionView questionView,
            QuestionDB screenQuestionDB);

    public abstract void instanceOfMultiQuestion(IQuestionView questionView,
            QuestionDB screenQuestionDB);

    public abstract void renderParticularSurvey(QuestionDB screenQuestionDB, SurveyDB surveyDB,
            IQuestionView questionView);

    public abstract boolean isMultiQuestionByVariant(int tabType);

    public abstract void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView);

    public void finishOrNext(final boolean readOnly) {
        try {
            System.out.println(Session.getMalariaSurveyDB().getValuesFromDB().toString());
            if (Session.getStockSurveyDB() != null) {
                System.out.println(Session.getStockSurveyDB().getValuesFromDB().toString());
            }
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

        QuestionDB questionDB = mDynamicTabAdapter.navigationController.getCurrentQuestion();
        ValueDB valueDB = questionDB.getValueBySession();
        if (mDynamicTabAdapter.isDone(valueDB)) {
            mDynamicTabAdapter.navigationController.isMovingToForward = false;
            if (readOnly) {
                DashboardActivity.dashboardActivity.initReview(readOnly);
            } else if (!shouldShowReviewScreen() || !BuildConfig.reviewScreen) {
                mDynamicTabAdapter.surveyShowDone();
            } else {
                DashboardActivity.dashboardActivity.showReviewFragment();
                CommonQuestionView.hideKeyboard(
                        PreferencesState.getInstance().getContext(),
                        mDynamicTabAdapter.getKeyboardView());
                DynamicTabAdapter.setIsClicked(false);
            }
            return;
        }
        mDynamicTabAdapter.next();
        Log.d("NextText", "End next");

    }

    public abstract void addScrollToSwipeTouchListener(View rowView);

    protected boolean shouldShowReviewScreen() {
        return getMalariaSurveyDB().isRDT() || BuildConfig.patientTestedByDefault;
    }

    public void initNavigationButtons(boolean readOnly, View nextButton) {
    }

    public void onOrgUnitDropdownAnswered(OptionDB selectedOptionDB) {
    }

    public void evaluateTreatmentMatch(QuestionDB questionDB, OptionDB selectedOptionDB,
            QuestionRelationDB questionRelationDB) {

    }

    public void showValidationErrors() {
    }
}
