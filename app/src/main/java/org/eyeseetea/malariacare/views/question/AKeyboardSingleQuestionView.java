package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AKeyboardSingleQuestionView extends AKeyboardQuestionView {

    String lastValue="";
    public AKeyboardSingleQuestionView(Context context) {
        super(context);
    }

    protected void notifyAnswerChanged(String newValue) {
        if(!newValue.equals(lastValue)) {
            super.notifyAnswerChanged(newValue);
            lastValue=newValue;
        }
    }
}
