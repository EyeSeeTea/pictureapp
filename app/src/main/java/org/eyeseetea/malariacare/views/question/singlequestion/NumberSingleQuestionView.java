package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies.ANumberSingleQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies.NumberSingleQuestionViewStrategyStrategy;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class NumberSingleQuestionView extends AKeyboardSingleQuestionView implements
        IQuestionView {
    CustomButton sendButton;
    private ANumberSingleQuestionViewStrategy mQuestionViewStrategy;

    public NumberSingleQuestionView(Context context) {
        super(context);
        mQuestionViewStrategy =
                new NumberSingleQuestionViewStrategyStrategy();

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
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_int_row, this);

        answer = (CustomEditText) findViewById(R.id.answer);
        answer.setFocusable(true);
        answer.setFocusableInTouchMode(true);

        Validation.getInstance().addInput(answer);
        sendButton = (CustomButton) findViewById(R.id.dynamic_positiveInt_btn);

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(context);
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

    public void setQuestionDB(QuestionDB questionDB) {
        mQuestionViewStrategy.setQuestionDB(this, questionDB);
    }

    @Override
    public void validateAnswer(Context context) {
        try {
            int value = Integer.parseInt(answer.getText().toString());
            if(validateQuestionRegExp(answer)) {
                Validation.getInstance().removeInputError(answer);
                validateQuestionRegExp(answer);
                hideKeyboard(answer);
                notifyAnswerChanged(String.valueOf(value));
            }
        } catch (NumberFormatException e) {
            Validation.getInstance().addinvalidInput(answer,
                    context.getString(R.string.dynamic_error_number));
            answer.setError(context.getString(R.string.dynamic_error_number));
        }
    }
}
