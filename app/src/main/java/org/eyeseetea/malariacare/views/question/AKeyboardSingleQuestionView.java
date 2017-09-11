package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AKeyboardSingleQuestionView extends AKeyboardQuestionView {

    Boolean isClicked = false;
    String lastValue = null;
    public AKeyboardSingleQuestionView(Context context) {
        super(context);
    }

    protected void notifyAnswerChanged(String newValue) {
        if (lastValue == null || !newValue.equals(lastValue)) {
            super.notifyAnswerChanged(newValue);
            lastValue = newValue;
        }
    }

    public void action(Context context) {
        if (!isClicked) {
            isClicked = true;
            validateAnswer(context);
            isClicked = false;
        }
    }

    protected abstract void validateAnswer(Context context);
}
