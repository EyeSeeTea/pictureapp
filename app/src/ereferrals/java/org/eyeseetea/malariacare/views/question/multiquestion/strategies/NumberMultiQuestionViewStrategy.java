package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.NumberMultiQuestionView;

public class NumberMultiQuestionViewStrategy extends ANumberMultiquestionViewStrategy {
    public NumberMultiQuestionViewStrategy(
            NumberMultiQuestionView numberMultiQuestionView) {
        super(numberMultiQuestionView);
    }

    @Override
    public void init() {
        EditText editText = (EditText) mNumberMultiQuestionView.findViewById(R.id.answer);
        Validation.getInstance().addinvalidInput(editText,
                mNumberMultiQuestionView.getContext().getString(
                        R.string.dynamic_error_number));
    }

    @Override
    public void afterAnswerChange() {
    }
}
