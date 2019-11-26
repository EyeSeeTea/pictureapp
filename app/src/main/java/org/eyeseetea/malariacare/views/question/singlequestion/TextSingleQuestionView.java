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
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies
        .ATextSingleQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies
        .TextSingleQuestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomButton;
import org.eyeseetea.sdk.presentation.views.CustomEditText;


public class TextSingleQuestionView extends AKeyboardSingleQuestionView implements IQuestionView {
    CustomEditText mEditText;
    CustomButton sendButton;
    ATextSingleQuestionViewStrategy mTextSingleQuestionViewStrategy;

    public TextSingleQuestionView(Context context) {
        super(context);
        mTextSingleQuestionViewStrategy = new TextSingleQuestionViewStrategy();
        init(context);
    }

    @Override
    protected void validateAnswer(Context context) {

    }

    @Override
    public EditText getAnswerView() {
        return mEditText;
    }

    @Override
    public boolean isEnabled(){
        return mEditText.isEnabled();
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
                saveAnswer();
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveAnswer();
                    return true;
                }

                return false;
            }
        });
    }

    private void saveAnswer() {
        hideKeyboard(mEditText);
        if(validateQuestionRegExp(mEditText)) {
            notifyAnswerChanged(mEditText.getText().toString());
        }
    }

    @Override
    public void setQuestionDB(QuestionDB questionDB) {
        mTextSingleQuestionViewStrategy.setQuestionDB(this, questionDB);
    }
}
