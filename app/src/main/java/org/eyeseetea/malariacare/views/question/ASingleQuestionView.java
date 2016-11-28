package org.eyeseetea.malariacare.views.question;

import static com.google.android.gms.analytics.internal.zzy.e;

import android.content.Context;

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
}
