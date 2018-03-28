package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.TextMultiQuestionView;

public class TextMultiQuestionViewStrategy extends ATextMultiQuestionViewStrategy {
    @Override
    public void init(TextMultiQuestionView textMultiQuestionView) {
        EditText editText = (EditText) textMultiQuestionView.findViewById(R.id.answer);
        Validation.getInstance().addInput(editText);
        Validation.getInstance().addinvalidInput(editText,
                textMultiQuestionView.getContext().getString(
                        R.string.error_empty_question));
    }

    @Override
    public void afterTextChange(TextMultiQuestionView textMultiQuestionView) {
        EditText editText = (EditText) textMultiQuestionView.findViewById(R.id.answer);
        if (!editText.getText().toString().isEmpty()) {
            Validation.getInstance().removeInputError(editText);
        } else {
            Validation.getInstance().addinvalidInput(editText,
                    textMultiQuestionView.getContext().getString(
                            R.string.error_empty_question));
        }
    }
}
