package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;

public abstract class AKeyboardQuestionView extends CommonQuestionView implements IExtraValidation {
    protected onAnswerChangedListener mOnAnswerChangedListener;
    public EditText answer;

    public AKeyboardQuestionView(Context context) {
        super(context);
    }

    @Override
    public void setJumpingNextQuestionActive(boolean jumpingNextQuestionActive) {
        super.setJumpingNextQuestionActive(jumpingNextQuestionActive);

        if (jumpingNextQuestionActive){
            answer.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            answer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    public void setOnAnswerChangedListener(onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
        initEditTextActionListener();
    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
    }

    public void checkLoadedErrors() {
        if(answer.getText().toString().isEmpty() && !question.isCompulsory()){
            Validation.getInstance().removeInputError(answer);
        }
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }

    public abstract EditText getAnswerView();

    private void initEditTextActionListener() {
        EditText editText = this.getAnswerView();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                IQuestionView nextQuestionView = getNextQuestionView();
                 if (nextQuestionView == null || nextQuestionView instanceof AKeyboardQuestionView
                         || !(nextQuestionView instanceof IMultiQuestionView)) {
                     return false;
                 }
                if (actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    CommonQuestionView.hideKeyboard(textView.getContext(), textView);
                    textView.clearFocus();
                    focusNextQuestion();
                }
                return true;
            }
        });
    }
    protected void validateAnswer(String value, TextView textView) {
        if (BuildConfig.validationInline) {
            if (!value.isEmpty()) {
                if(validateQuestionRegExp(textView)) {
                    Validation.getInstance().removeInputError(textView);
                }
            }
            else if(!question.isCompulsory()){
                Validation.getInstance().removeInputError(textView);
            } else {
                Validation.getInstance().addinvalidInput(textView, getContext().getString(
                        R.string.error_empty_question));
            }
        }
    }
}
