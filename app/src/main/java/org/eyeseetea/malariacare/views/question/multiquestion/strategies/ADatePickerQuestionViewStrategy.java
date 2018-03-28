package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.DatePickerQuestionView;

public abstract class ADatePickerQuestionViewStrategy {

     protected DatePickerQuestionView mDatePickerQuestionView;

    public ADatePickerQuestionViewStrategy(
            DatePickerQuestionView datePickerQuestionView) {
        mDatePickerQuestionView = datePickerQuestionView;
    }

    public void init() {

    }

    public void onAnswerChange() {

    }
}
