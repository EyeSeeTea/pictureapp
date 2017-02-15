package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
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
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mCustomEditText.setEnabled(enabled);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            mCustomEditText.setText(value.getValue());
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


    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_text_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mCustomEditText = (CustomEditText) findViewById(R.id.answer);

        mCustomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notifyAnswerChanged(String.valueOf(s));
            }
        });
    }

    public void setInputType(int value) {
        mCustomEditText.setInputType(value | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }
}
