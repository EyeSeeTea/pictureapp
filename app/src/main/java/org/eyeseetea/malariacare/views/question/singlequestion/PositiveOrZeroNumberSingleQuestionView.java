package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.PositiveOrZeroNumber;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveOrZeroNumberException;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class PositiveOrZeroNumberSingleQuestionView  extends AKeyboardSingleQuestionView implements
        IQuestionView {
    CustomEditText numberPicker;
    CustomButton sendButton;
    Boolean isClicked=false;

    public PositiveOrZeroNumberSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        numberPicker.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            showKeyboard(numberPicker);
        }
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

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_positiveint_row, this);

        numberPicker = (CustomEditText) findViewById(R.id.answer);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);

        Validation.getInstance().addInput(numberPicker);
        sendButton = (CustomButton) findViewById(R.id.dynamic_positiveInt_btn);

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(context);
            }
        });

        numberPicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateAnswer(context);
                    return true;
                }

                return false;
            }
        });
    }

    protected void validateAnswer(Context context) {
        if(!isClicked) {
            isClicked = true;
            try {
                PositiveOrZeroNumber positiveNumber = PositiveOrZeroNumber.parse(
                        numberPicker.getText().toString());
                Validation.getInstance().removeInputError(numberPicker);
                hideKeyboard(numberPicker);
                notifyAnswerChanged(String.valueOf(positiveNumber.getValue()));
            } catch (InvalidPositiveOrZeroNumberException e) {
                Validation.getInstance().addinvalidInput(numberPicker,
                        context.getString(R.string.dynamic_error_age));
                numberPicker.setError(context.getString(R.string.dynamic_error_age));
            }
            isClicked = false;
        }
    }
}
