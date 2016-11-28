package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.PositiveNumber;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveNumberException;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.question.ASingleQuestionView;

public class PositiveNumberSingleQuestionView extends ASingleQuestionView {
    EditText numberPicker;
    Button sendButton;

    public PositiveNumberSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        numberPicker.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled)
            showKeyboard(numberPicker);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            numberPicker.setText(value.getValue());
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_positiveint_row, this);

        numberPicker = (EditCard) findViewById(R.id.answer);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);

        sendButton = (Button) findViewById(R.id.dynamic_positiveInt_btn);

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

/*        if (!isMultipleQuestionTab(tabType)) {
            //Add button to listener
            swipeTouchListener.addClickableView(button);
        }*/
    }

    private void validateAnswer(Context context) {
        try {
            PositiveNumber positiveNumber = PositiveNumber.parse(numberPicker.getText().toString());
            hideKeyboard(numberPicker);
            notifyAnswerChanged(String.valueOf(positiveNumber.getValue()));

        } catch (InvalidPositiveNumberException e) {
            numberPicker.setError(context.getString(R.string.dynamic_error_age));
        }
    }

}
