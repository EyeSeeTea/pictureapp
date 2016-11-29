package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AKeyboardSingleQuestionView extends AKeyboardQuestionView {
    boolean alreadyNotified;

    public AKeyboardSingleQuestionView(Context context) {
        super(context);
    }

    protected void notifyAnswerChanged(String newValue) {
        if (!alreadyNotified) {
            alreadyNotified = true;
            super.notifyAnswerChanged(newValue);
        }
    }
}
