package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

import java.util.List;

public abstract class AOptionQuestionView extends CommonQuestionView {
    private onAnswerChangedListener mOnAnswerChangedListener;

    public AOptionQuestionView(Context context) {
        super(context);
    }

    public abstract void setOptions(List<OptionDB> optionDBs);

    public abstract void setQuestionDB(QuestionDB questionDB);

    public void setOnAnswerChangedListener(onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    protected void notifyAnswerChanged(OptionDB optionDB) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, optionDB);
        }
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, OptionDB optionDB);
    }
}
