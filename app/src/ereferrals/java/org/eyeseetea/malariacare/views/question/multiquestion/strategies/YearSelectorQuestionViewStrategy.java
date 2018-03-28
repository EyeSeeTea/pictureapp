package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.YearSelectorQuestionView;

public class YearSelectorQuestionViewStrategy extends AYearSelectorQuestionViewStrategy {
    public YearSelectorQuestionViewStrategy(
            YearSelectorQuestionView yearSelectorQuestionView) {
        super(yearSelectorQuestionView);
    }

    @Override
    public void init() {
        TextView textView = (TextView) mYearSelectorQuestionView.findViewById(R.id.answer);
        Validation.getInstance().addInput(textView);
        Validation.getInstance().addinvalidInput(textView,
                mYearSelectorQuestionView.getContext().getString(
                        R.string.error_empty_question));
    }

    @Override
    public void afterTextChange() {
        TextView textView = (TextView) mYearSelectorQuestionView.findViewById(R.id.answer);
        if (!textView.getText().toString().isEmpty()) {
            Validation.getInstance().removeInputError(textView);
            textView.setError(null);
        } else {
            Validation.getInstance().addinvalidInput(textView,
                    mYearSelectorQuestionView.getContext().getString(
                            R.string.error_empty_question));
        }
    }
}
