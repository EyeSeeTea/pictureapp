package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class PhoneSingleQuestionView extends AKeyboardSingleQuestionView implements IQuestionView {
    CustomButton sendButton;

    public PhoneSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return answer;
    }

    @Override
    public void setEnabled(boolean enabled) {
        answer.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            showKeyboard(answer);
        }
    }

    @Override
    public void setHelpText(String helpText) {
        answer.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            answer.setText(valueDB.getValue());
        }
        if(answer.getText().toString().isEmpty() && !question.isCompulsory()){
            Validation.getInstance().removeInputError(answer);
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_phone_row, this);

        answer = (CustomEditText) findViewById(R.id.answer);
        answer.setFocusable(true);
        answer.setFocusableInTouchMode(true);

        sendButton = (CustomButton) findViewById(R.id.row_phone_btn);

        Validation.getInstance().addInput(answer);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action(context);
            }
        });

        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    action(context);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void validateAnswer(Context context) {
        try {
            Phone phone = new Phone(answer.getText().toString());
            hideKeyboard(answer);

            if(validateQuestionRegExp(answer)) {
                Validation.getInstance().removeInputError(answer);
                String value = phone.getValue();
                notifyAnswerChanged(value);
            }
        } catch (InvalidPhoneException e) {
            Validation.getInstance().addinvalidInput(answer,
                    context.getString(R.string.dynamic_error_phone_format));
            answer.setError(context.getString(R.string.dynamic_error_phone_format));
        }
        if(answer.getText().toString().isEmpty() && !question.isCompulsory()){
            Validation.getInstance().removeInputError(answer);
        }
    }
}
