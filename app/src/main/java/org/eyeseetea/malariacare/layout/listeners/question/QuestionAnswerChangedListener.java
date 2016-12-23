package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;


public class QuestionAnswerChangedListener extends AQuestionAnswerChangedListener implements
        AKeyboardQuestionView.onAnswerChangedListener, AOptionQuestionView.onAnswerChangedListener {

    private DynamicTabAdapter mDynamicTabAdapter;
    private boolean mAdvanceToNextQuestion;

    public QuestionAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter, boolean advanceToNextQuestion) {
        super(tableLayout);

        mDynamicTabAdapter = dynamicTabAdapter;
        mAdvanceToNextQuestion = advanceToNextQuestion;
    }

    @Override
    public void onAnswerChanged(View view, String newValue) {
        saveValue(view, newValue);

        if (mAdvanceToNextQuestion) {
            mDynamicTabAdapter.navigationController.isMovingToForward = true;
            mDynamicTabAdapter.finishOrNext();
        }
    }

    @Override
    public void onAnswerChanged(View view, Option option) {
        if (mAdvanceToNextQuestion) {
            mDynamicTabAdapter.OnOptionAnswered(view, option);
        } else {
            saveValue(view, option);
        }
    }

}
