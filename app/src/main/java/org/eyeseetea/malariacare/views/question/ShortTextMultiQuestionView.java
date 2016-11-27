package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;

public class ShortTextMultiQuestionView extends AMultiQuestionView {
    TextCard header;
    EditCard editCard;

    public ShortTextMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
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

    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_short_text_row, this);

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
                //TODO: this in onTextChanged y not in afterTextChanged?
                notifyAnswerChanged(String.valueOf(s));
            }
        });

        //TODO: At the moment it is only used in MultipleQuestionTab, is necessary this if?
/*        if (!isMultipleQuestionTab(tabType)) {
            //Take focus and open keyboard
            openKeyboard(editCard);
        }*/
    }
}
