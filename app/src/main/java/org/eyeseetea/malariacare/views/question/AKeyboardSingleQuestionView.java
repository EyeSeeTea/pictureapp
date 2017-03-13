package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AKeyboardSingleQuestionView extends AKeyboardQuestionView {

    public AKeyboardSingleQuestionView(Context context) {
        super(context);
    }

    protected void notifyAnswerChanged(String newValue) {
            super.notifyAnswerChanged(newValue);
    }
}
