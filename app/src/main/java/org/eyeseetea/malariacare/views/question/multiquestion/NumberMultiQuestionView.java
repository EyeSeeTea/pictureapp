package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class NumberMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    CustomEditText numberPicker;

    public NumberMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        numberPicker.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {
        numberPicker.setHint(helpText);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            numberPicker.setText(value.getValue());
        }
    }

    @Override
    public boolean hasError() {
        return numberPicker.getError() != null;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_int_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        numberPicker = (CustomEditText) findViewById(R.id.answer);

        Validation.getInstance().addInput(numberPicker);
        numberPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                try {
                    int value = Integer.parseInt(s.toString());
                    notifyAnswerChanged(String.valueOf(value));

                } catch (NumberFormatException e) {
                    Validation.getInstance().addinvalidInput(numberPicker,
                            context.getString(R.string.dynamic_error_number));
                    //numberPicker.setError(context.getString(R.string.dynamic_error_number));
                }

                notifyAnswerChanged(numberPicker.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }
}
