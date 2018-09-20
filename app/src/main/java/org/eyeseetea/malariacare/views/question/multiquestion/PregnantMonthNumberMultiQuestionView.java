package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.PregnantMonthNumber;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPregnantMonthNumberException;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PregnantMonthNumberMultiQuestionView extends AKeyboardQuestionView implements
        IQuestionView,
        IMultiQuestionView {
    CustomTextView header;

    PregnantMonthNumber monthNumber;

    public PregnantMonthNumberMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public EditText getAnswerView() {
        return answer;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        answer.setEnabled(enabled);
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

    @Override
    public boolean hasError() {
        return answer.getError() != null || monthNumber == null;
    }

    @Override
    public void requestAnswerFocus() {
        answer.requestFocus();
        showKeyboard(getContext(), answer);
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_pregnant_month_int_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        answer = (CustomEditText) findViewById(R.id.answer);
        Validation.getInstance().addInput(answer);
        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    monthNumber = PregnantMonthNumber.parse(
                            answer.getText().toString());
                    if(validateQuestionRegExp(answer)) {
                        notifyAnswerChanged(String.valueOf(monthNumber.getValue()));
                        Validation.getInstance().removeInputError(answer);
                    }

                } catch (InvalidPregnantMonthNumberException e) {
                    Validation.getInstance().addinvalidInput(answer,
                            context.getString(R.string.dynamic_error_pregnant_month));
                }
                if(answer.getText().toString().isEmpty() && !question.isCompulsory()){
                    Validation.getInstance().removeInputError(answer);
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
