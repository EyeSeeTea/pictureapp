package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.DatePickerQuestionView;

public class DatePickerQuestionViewStrategy extends ADatePickerQuestionViewStrategy {
    public DatePickerQuestionViewStrategy(
            DatePickerQuestionView datePickerQuestionView) {
        super(datePickerQuestionView);
    }

    @Override
    public void init() {
        TextView textView = (TextView) mDatePickerQuestionView.findViewById(R.id.answer);
        Validation.getInstance().addInput(textView);
        Validation.getInstance().addinvalidInput(textView,
                mDatePickerQuestionView.getContext().getString(
                        R.string.error_empty_question));
    }

    @Override
    public void onAnswerChange() {
        TextView textView = (TextView) mDatePickerQuestionView.findViewById(R.id.answer);
        if (textView.getText().toString().isEmpty()) {
            Validation.getInstance().addinvalidInput(textView,
                    mDatePickerQuestionView.getContext().getString(
                            R.string.error_empty_question));
        } else {
            Validation.getInstance().removeInputError(textView);
            textView.setError(null);
        }
    }
}
