package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.database.model.Value;

public abstract class AQuestionView extends LinearLayout {
    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }

    public abstract void setEnabled(boolean enabled);

    public abstract void setValue(Value value);

    protected onAnswerChangedListener mOnAnswerChangedListener;

    public AQuestionView(Context context) {
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


}
