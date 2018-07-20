package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.PositiveOrZeroNumber;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveOrZeroNumberException;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PositiveOrZeroNumberMultiQuestionView extends AKeyboardQuestionView implements
        IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    CustomEditText numberPicker;
    PositiveOrZeroNumber positiveOrZeroNumber;

    public PositiveOrZeroNumberMultiQuestionView(Context context) {
        super(context);
        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return numberPicker;
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
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            numberPicker.setText(valueDB.getValue());
        }
    }

    @Override
    public boolean hasError() {
        return numberPicker.getError() != null || positiveOrZeroNumber == null;
    }

    @Override
    public void requestAnswerFocus() {
        numberPicker.requestFocus();
        showKeyboard(getContext(), numberPicker);
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_positive_int_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        numberPicker = (CustomEditText) findViewById(R.id.answer);

        if (BuildConfig.validationInline) {
            if (!numberPicker.getText().toString().isEmpty()) {
                Validation.getInstance().removeInputError(numberPicker);
            } else {
                Validation.getInstance().addinvalidInput(numberPicker, getContext().getString(
                        R.string.error_empty_question));
            }
        }
        Validation.getInstance().addInput(numberPicker);
        numberPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    positiveOrZeroNumber = PositiveOrZeroNumber.parse(
                            numberPicker.getText().toString());
                    notifyAnswerChanged(String.valueOf(positiveOrZeroNumber.getValue()));
                    Validation.getInstance().removeInputError(numberPicker);

                } catch (InvalidPositiveOrZeroNumberException e) {
                    Validation.getInstance().addinvalidInput(numberPicker,
                            context.getString(R.string.dynamic_error_invalid_positive_number));
                }
                if (BuildConfig.validationInline) {
                    Validation.getInstance().addInput(numberPicker);
                    Validation.getInstance().addinvalidInput(numberPicker, getContext().getString(
                            R.string.error_empty_question));
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
