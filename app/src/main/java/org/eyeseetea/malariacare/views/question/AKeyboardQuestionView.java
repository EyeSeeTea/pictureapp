package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public abstract class AKeyboardQuestionView extends CommonQuestionView {
    protected onAnswerChangedListener mOnAnswerChangedListener;

    public AKeyboardQuestionView(Context context) {
        super(context);
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
}
