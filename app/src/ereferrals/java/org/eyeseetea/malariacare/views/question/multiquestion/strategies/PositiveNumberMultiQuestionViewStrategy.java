package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.PositiveNumberMultiQuestionView;

public class PositiveNumberMultiQuestionViewStrategy extends
        APositiveNumberMultiQuestionViewStrategy {
    public PositiveNumberMultiQuestionViewStrategy(
            PositiveNumberMultiQuestionView positiveNumberMultiQuestionView) {
        super(positiveNumberMultiQuestionView);
    }

    @Override
    public void init() {
        EditText editText = (EditText) mPositiveNumberMultiQuestionView.findViewById(R.id.answer);
        Validation.getInstance().addInput(editText);
        Validation.getInstance().addinvalidInput(editText,
                mPositiveNumberMultiQuestionView.getContext().getString(
                        R.string.error_empty_question));
    }

    @Override
    public void beforeAnswerChange() {
        EditText editText = (EditText) mPositiveNumberMultiQuestionView.findViewById(R.id.answer);
        if (!editText.getText().toString().isEmpty()) {
            Validation.getInstance().removeInputError(editText);
        } else {
            Validation.getInstance().addinvalidInput(editText,
                    mPositiveNumberMultiQuestionView.getContext().getString(
                            R.string.error_empty_question));
        }
    }
}
