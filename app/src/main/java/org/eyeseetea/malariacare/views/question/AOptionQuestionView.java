package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;

import java.util.List;

public abstract class AOptionQuestionView extends CommonQuestionView {
    private onAnswerChangedListener mOnAnswerChangedListener;

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
