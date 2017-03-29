package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class PhoneSingleQuestionView extends AKeyboardSingleQuestionView implements IQuestionView {
    CustomEditText mCustomEditText;
    CustomButton sendButton;

    public PhoneSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mCustomEditText.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            showKeyboard(mCustomEditText);
        }
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

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_phone_row, this);

        mCustomEditText = (CustomEditText) findViewById(R.id.answer);
        mCustomEditText.setFocusable(true);
        mCustomEditText.setFocusableInTouchMode(true);

        sendButton = (CustomButton) findViewById(R.id.row_phone_btn);

        Validation.getInstance().addInput(mCustomEditText);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action(context);
            }
        });

        mCustomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void validateAnswer(Context context) {
        try {
            Phone phone = new Phone(mCustomEditText.getText().toString());
            hideKeyboard(mCustomEditText);
            Validation.getInstance().removeInputError(mCustomEditText);
            String value = phone.getValue();
            notifyAnswerChanged(value);
        } catch (InvalidPhoneException e) {
            Validation.getInstance().addinvalidInput(mCustomEditText,
                    context.getString(R.string.dynamic_error_phone_format));
            mCustomEditText.setError(context.getString(R.string.dynamic_error_phone_format));
        }
    }
}
