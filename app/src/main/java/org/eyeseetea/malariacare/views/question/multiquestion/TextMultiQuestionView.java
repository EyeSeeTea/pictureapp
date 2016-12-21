package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

public class TextMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    TextCard header;
    EditCard editCard;

    public TextMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        editCard.setEnabled(enabled);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            editCard.setText(value.getValue());
        }
    }

    public void setInputType(int value) {
        editCard.setInputType(value);
    }

    @Override
    public void setHelpText(String helpText) {
        editCard.setHint(helpText);
    }

    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_text_row, this);

        header = (TextCard) findViewById(R.id.row_header_text);
        editCard = (EditCard) findViewById(R.id.answer);

        editCard.addTextChangedListener(new TextWatcher() {
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
}
