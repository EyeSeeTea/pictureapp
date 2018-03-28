package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.NumberMultiQuestionView;

public abstract class ANumberMultiquestionViewStrategy {
    protected NumberMultiQuestionView mNumberMultiQuestionView;

    public ANumberMultiquestionViewStrategy(
            NumberMultiQuestionView numberMultiQuestionView) {
        mNumberMultiQuestionView = numberMultiQuestionView;
    }

    public void init() {
    }

    public void afterAnswerChange() {
    }
}
