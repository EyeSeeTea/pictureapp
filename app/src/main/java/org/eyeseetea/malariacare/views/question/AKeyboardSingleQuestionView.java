package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AKeyboardSingleQuestionView extends AKeyboardQuestionView {
    public AKeyboardSingleQuestionView(Context context) {
        super(context);
    }

    boolean alreadyNotified;

    protected void notifyAnswerChanged(String newValue) {
        if (!alreadyNotified) {
            alreadyNotified = true;
            super.notifyAnswerChanged(newValue);
        }
    }
}
