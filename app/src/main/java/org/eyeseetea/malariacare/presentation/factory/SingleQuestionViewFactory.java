package org.eyeseetea.malariacare.presentation.factory;


import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.listeners.question.SingleQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.PhoneSingleQuestionView;

public class SingleQuestionViewFactory implements IQuestionViewFactory{
    public AQuestionView getView(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.PHONE:
                return new PhoneSingleQuestionView(context);
        }

        throw new IllegalArgumentException("Not exists any question for type " + typeQuestion);
    }

    @Override
    public AQuestionView.onAnswerChangedListener getAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter) {
        return new SingleQuestionAnswerChangedListener(tableLayout,dynamicTabAdapter);
    }
}
