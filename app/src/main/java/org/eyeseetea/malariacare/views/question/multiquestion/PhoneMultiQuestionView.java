package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PhoneMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    CustomEditText mCustomEditText;

    public PhoneMultiQuestionView(Context context) {
        super(context);

        init(context);
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
    public void setHelpText(String helpText) {
        mCustomEditText.setHint(helpText);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            mCustomEditText.setText(value.getValue());
        }
    }

    @Override
    public boolean hasError() {
        return mCustomEditText.getError() != null;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_phone_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mCustomEditText = (CustomEditText) findViewById(R.id.answer);

        Validation.getInstance().addInput(mCustomEditText);
        mCustomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Phone phone = new Phone(mCustomEditText.getText().toString());
                    notifyAnswerChanged(phone.getValue());
                    Validation.getInstance().removeInputError(mCustomEditText);

                } catch (InvalidPhoneException e) {
                    Validation.getInstance().addinvalidInput(mCustomEditText,
                            context.getString(R.string.dynamic_error_phone_format));
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
