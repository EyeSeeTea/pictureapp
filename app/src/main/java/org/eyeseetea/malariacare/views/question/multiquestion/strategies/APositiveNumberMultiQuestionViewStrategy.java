package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.PositiveNumberMultiQuestionView;

public abstract class APositiveNumberMultiQuestionViewStrategy {
    protected PositiveNumberMultiQuestionView mPositiveNumberMultiQuestionView;

    public APositiveNumberMultiQuestionViewStrategy(
            PositiveNumberMultiQuestionView positiveNumberMultiQuestionView) {
        mPositiveNumberMultiQuestionView = positiveNumberMultiQuestionView;
    }

    public void init() {
    }

    public void beforeAnswerChange() {
    }
}
