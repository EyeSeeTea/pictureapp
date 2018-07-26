package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.PositiveNumber;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveNumberException;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PositiveNumberMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    PositiveNumber positiveNumber;

    public PositiveNumberMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return answer;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        answer.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {
        answer.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            answer.setText(valueDB.getValue());
            validateAnswer(answer.getText().toString(), answer);
        }
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void requestAnswerFocus() {
        answer.requestFocus();
        showKeyboard(getContext(), answer);
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_positive_int_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        answer = (CustomEditText) findViewById(R.id.answer);

        if (BuildConfig.validationInline) {
            Validation.getInstance().addInput(answer);
            Validation.getInstance().addinvalidInput(answer, getContext().getString(
                    R.string.error_empty_question));
        }
        Validation.getInstance().addInput(answer);
        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        positiveNumber = PositiveNumber.parse(answer.getText().toString());
                        notifyAnswerChanged(String.valueOf(positiveNumber.getValue()));
                    } else {
                        notifyAnswerChanged(answer.getText().toString());
                    }
                    if(validateQuestionRegExp(answer)) {
                        Validation.getInstance().removeInputError(answer);
                    }

                } catch (InvalidPositiveNumberException e) {
                    Validation.getInstance().addinvalidInput(answer,
                            context.getString(R.string.dynamic_error_invalid_positive_number));
                }

                validateAnswer(answer.getText().toString(), answer);
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
