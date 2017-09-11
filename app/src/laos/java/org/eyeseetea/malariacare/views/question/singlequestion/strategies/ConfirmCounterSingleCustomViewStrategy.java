package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.strategies.ConfirmCounterCommonStrategy;

public class ConfirmCounterSingleCustomViewStrategy implements
        IConfirmCounterSingleCustomViewStrategy {
    DynamicTabAdapter mDynamicTabAdapter;

    public ConfirmCounterSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void showConfirmCounter(final View view, final Option selectedOption,
            Question question, Question questionCounter) {

        ConfirmCounterCommonStrategy confirmCounterCommonStrategy =
                new ConfirmCounterCommonStrategy(mDynamicTabAdapter);

        confirmCounterCommonStrategy.showStandardConfirmCounter(view, selectedOption, question,
                questionCounter);
    }
}
