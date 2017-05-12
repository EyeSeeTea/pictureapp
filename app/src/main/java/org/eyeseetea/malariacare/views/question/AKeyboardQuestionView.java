package org.eyeseetea.malariacare.views.question;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public abstract class AKeyboardQuestionView extends LinearLayout {
    protected onAnswerChangedListener mOnAnswerChangedListener;

    public AKeyboardQuestionView(Context context) {
        super(context);
    }

    public void setOnAnswerChangedListener(onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
    }

    protected void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * hide keyboard using a keyboardView variable view
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }
}
