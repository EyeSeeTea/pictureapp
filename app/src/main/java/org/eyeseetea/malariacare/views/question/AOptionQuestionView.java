package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;

import java.util.List;

public abstract class AOptionQuestionView extends LinearLayout {
    onAnswerChangedListener mOnAnswerChangedListener;

    public AOptionQuestionView(Context context) {
        super(context);
    }

    public abstract void setOptions(List<Option> options);

    public abstract void setQuestion(Question question);

    public void setOnAnswerChangedListener(onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    protected void notifyAnswerChanged(Option option) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, option);
        }
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, Option option);
    }
}
