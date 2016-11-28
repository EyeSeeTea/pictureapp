package org.eyeseetea.malariacare.views.question;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static com.google.android.gms.analytics.internal.zzy.e;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class ASingleQuestionView extends AQuestionView {
    public ASingleQuestionView(Context context) {
        super(context);
    }

    boolean alreadyNotified;

    protected void notifyAnswerChanged(String newValue) {
        if (!alreadyNotified) {
            alreadyNotified = true;
            super.notifyAnswerChanged(newValue);
        }
    }

    protected void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    /**
     * hide keyboard using a keyboardView variable view
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
