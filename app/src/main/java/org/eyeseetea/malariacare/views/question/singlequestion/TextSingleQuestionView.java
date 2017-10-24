package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;


public class TextSingleQuestionView extends AKeyboardQuestionView implements IQuestionView {
    CustomEditText mEditText;
    CustomButton sendButton;

    public TextSingleQuestionView(Context context) {
        super(context);
        init(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEditText.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            showKeyboard(mEditText);
        }
    }

    @Override
    public void setHelpText(String helpText) {
        mEditText.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            mEditText.setText(valueDB.getValue());
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_single_question_text, this);

        mEditText = (CustomEditText) findViewById(R.id.answer);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);

        sendButton = (CustomButton) findViewById(R.id.dynamic_positiveInt_btn);

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyAnswerChanged(mEditText.getText().toString());
            }
        });
    }
}
