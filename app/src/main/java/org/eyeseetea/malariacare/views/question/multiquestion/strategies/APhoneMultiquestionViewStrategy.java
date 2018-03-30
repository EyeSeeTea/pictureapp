package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PhoneMultiQuestionView;

public abstract class APhoneMultiquestionViewStrategy {
    protected PhoneMultiQuestionView mPhoneMultiQuestionView;
    protected AKeyboardQuestionView.onAnswerChangedListener onAnswerChangedListener;

    public APhoneMultiquestionViewStrategy(
            PhoneMultiQuestionView phoneMultiQuestionView) {
        mPhoneMultiQuestionView = phoneMultiQuestionView;
    }

    public void setOnAnswerChangedListener(
            AKeyboardQuestionView.onAnswerChangedListener onAnswerChangedListener) {
        this.onAnswerChangedListener = onAnswerChangedListener;
    }

    public void addTextChangeListener(final EditText phoneText) {
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Phone phone = new Phone(phoneText.getText().toString());
                    notifyAnswerChanged(phone.getValue());
                    Validation.getInstance().removeInputError(phoneText);

                } catch (InvalidPhoneException e) {
                    Validation.getInstance().addinvalidInput(phoneText,
                            mPhoneMultiQuestionView.getContext().getString(
                                    R.string.dynamic_error_phone_format));
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

    protected void notifyAnswerChanged(String value) {
        if (onAnswerChangedListener != null) {
            onAnswerChangedListener.onAnswerChanged(mPhoneMultiQuestionView, value);
        }
    }

}
