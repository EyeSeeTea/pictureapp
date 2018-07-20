package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class TextMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    CustomEditText mCustomEditText;


    public TextMultiQuestionView(Context context) {
        super(context);
        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return mCustomEditText;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mCustomEditText.setEnabled(enabled);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            mCustomEditText.setText(valueDB.getValue());
            if (BuildConfig.validationInline) {
                if (!mCustomEditText.getText().toString().isEmpty()) {
                    Validation.getInstance().removeInputError(mCustomEditText);
                }
            }
        }
    }

    @Override
    public void setHelpText(String helpText) {
        mCustomEditText.setHint(helpText);
    }


    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void requestAnswerFocus() {
        mCustomEditText.requestFocus();
        showKeyboard(getContext(), mCustomEditText);
    }


    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_text_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mCustomEditText = (CustomEditText) findViewById(R.id.answer);

        mCustomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (BuildConfig.validationInline) {
                    if (!mCustomEditText.getText().toString().isEmpty()) {
                        Validation.getInstance().removeInputError(mCustomEditText);
                    } else {
                        Validation.getInstance().addinvalidInput(mCustomEditText,
                                getContext().getString(R.string.error_empty_question));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notifyAnswerChanged(String.valueOf(s));
            }
        });
        if (BuildConfig.validationInline) {
            Validation.getInstance().addInput(mCustomEditText);
            Validation.getInstance().addinvalidInput(mCustomEditText, getContext().getString(
                    R.string.error_empty_question));
        }
    }

    public void setInputType(int value) {
        mCustomEditText.setInputType(value | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }
}
