package org.eyeseetea.malariacare.views.question;

import android.content.Context;

public abstract class AMultiQuestionView extends AQuestionView {
    public AMultiQuestionView(Context context) {
        super(context);
    }

    public abstract void setHeader(String headerValue);

    public abstract boolean hasError();
}
