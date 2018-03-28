package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.PositiveOrZeroNumberMultiQuestionView;

public abstract class APositiveOrZeroNumberMultiQuestionViewStrategy {

    protected PositiveOrZeroNumberMultiQuestionView mPositiveOrZeroNumberMultiQuestionView;

    public APositiveOrZeroNumberMultiQuestionViewStrategy(
            PositiveOrZeroNumberMultiQuestionView positiveOrZeroNumberMultiQuestionView) {
        mPositiveOrZeroNumberMultiQuestionView = positiveOrZeroNumberMultiQuestionView;
    }

    public void afterTextChange(){
    }

    public void init(){

    }


}
