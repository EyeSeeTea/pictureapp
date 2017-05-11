package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;


public class QuestionAnswerChangedListener implements
        AKeyboardQuestionView.onAnswerChangedListener, AOptionQuestionView.onAnswerChangedListener {

    private DynamicTabAdapter mDynamicTabAdapter;
    private boolean mAdvanceToNextQuestion;

    public QuestionAnswerChangedListener(
            DynamicTabAdapter dynamicTabAdapter, boolean advanceToNextQuestion) {

        mDynamicTabAdapter = dynamicTabAdapter;
        mAdvanceToNextQuestion = advanceToNextQuestion;
    }

    @Override
    public void onAnswerChanged(View view, String newValue) {
        mDynamicTabAdapter.saveTextValue(view, newValue, mAdvanceToNextQuestion);
    }

    @Override
    public void onAnswerChanged(View view, Option option) {
        mDynamicTabAdapter.OnOptionAnswered(view, option, mAdvanceToNextQuestion);
    }

}
