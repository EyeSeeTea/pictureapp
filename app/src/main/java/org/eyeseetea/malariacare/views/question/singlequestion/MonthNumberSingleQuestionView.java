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
import org.eyeseetea.malariacare.domain.entity.AgeMonthNumber;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidAgeMonthNumberException;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies
        .AMonthNumberSingleQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies
        .MonthNumberSingleQuestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;


public class MonthNumberSingleQuestionView extends AKeyboardSingleQuestionView implements
        IQuestionView {
    CustomEditText numberPicker;
    CustomButton sendButton;
    Boolean isClicked = false;
    AMonthNumberSingleQuestionViewStrategy mMonthNumberSingleQuestionViewStrategy;

    public MonthNumberSingleQuestionView(Context context) {
        super(context);
        mMonthNumberSingleQuestionViewStrategy = new MonthNumberSingleQuestionViewStrategy();
        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return numberPicker;
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
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            numberPicker.setText(valueDB.getValue());
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_age_month_number, this);

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
        if (!isClicked) {
            isClicked = true;
            try {
                AgeMonthNumber ageMonthNumber = AgeMonthNumber.parse(
                        numberPicker.getText().toString());
                Validation.getInstance().removeInputError(numberPicker);
                hideKeyboard(numberPicker);
                notifyAnswerChanged(String.valueOf(ageMonthNumber.getValue()));
            } catch (InvalidAgeMonthNumberException e) {
                Validation.getInstance().addinvalidInput(numberPicker,
                        context.getString(R.string.dynamic_error_age));
                numberPicker.setError(context.getString(R.string.dynamic_error_month_age));
            }
            isClicked = false;
        }
    }

    public void setQuestionDB(QuestionDB questionDB) {
        mMonthNumberSingleQuestionViewStrategy.setQuestionDB(this, questionDB);
    }

}
