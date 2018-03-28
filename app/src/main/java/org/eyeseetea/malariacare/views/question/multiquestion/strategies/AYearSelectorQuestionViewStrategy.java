package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.YearSelectorQuestionView;

public abstract class AYearSelectorQuestionViewStrategy {

    protected YearSelectorQuestionView mYearSelectorQuestionView;

    public AYearSelectorQuestionViewStrategy(
            YearSelectorQuestionView yearSelectorQuestionView) {
        mYearSelectorQuestionView = yearSelectorQuestionView;
    }

    public void init() {
    }

    public void afterTextChange() {
    }
}
