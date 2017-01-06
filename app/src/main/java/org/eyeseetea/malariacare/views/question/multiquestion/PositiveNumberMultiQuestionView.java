package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.PositiveNumber;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveNumberException;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

public class PositiveNumberMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    TextCard header;
    EditCard numberPicker;

    PositiveNumber positiveNumber;

    public PositiveNumberMultiQuestionView(Context context) {
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
        return numberPicker.getError() != null || positiveNumber == null;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_positive_int_row, this);

        header = (TextCard) findViewById(R.id.row_header_text);
        numberPicker = (EditCard) findViewById(R.id.answer);

        numberPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    positiveNumber = PositiveNumber.parse(numberPicker.getText().toString());
                    notifyAnswerChanged(String.valueOf(positiveNumber.getValue()));

                } catch (InvalidPositiveNumberException e) {
                    numberPicker.setError(
                            context.getString(R.string.dynamic_error_age));
                }
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
