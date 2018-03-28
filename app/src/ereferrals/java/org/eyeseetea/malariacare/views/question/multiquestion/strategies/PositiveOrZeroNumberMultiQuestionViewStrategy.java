package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.PositiveOrZeroNumberMultiQuestionView;

public class PositiveOrZeroNumberMultiQuestionViewStrategy extends
        APositiveOrZeroNumberMultiQuestionViewStrategy {
    public PositiveOrZeroNumberMultiQuestionViewStrategy(
            PositiveOrZeroNumberMultiQuestionView positiveOrZeroNumberMultiQuestionView) {
        super(positiveOrZeroNumberMultiQuestionView);
    }

    @Override
    public void afterTextChange() {
        EditText editText = (EditText) mPositiveOrZeroNumberMultiQuestionView.findViewById(
                R.id.answer);
        Validation.getInstance().addInput(editText);
        Validation.getInstance().addinvalidInput(editText,
                mPositiveOrZeroNumberMultiQuestionView.getContext().getString(
                        R.string.error_empty_question));
    }

    @Override
    public void init() {
        EditText editText = (EditText) mPositiveOrZeroNumberMultiQuestionView.findViewById(
                R.id.answer);
        if (!editText.getText().toString().isEmpty()) {
            Validation.getInstance().removeInputError(editText);
        } else {
            Validation.getInstance().addinvalidInput(editText,
                    mPositiveOrZeroNumberMultiQuestionView.getContext().getString(
                            R.string.error_empty_question));
        }
    }
}
