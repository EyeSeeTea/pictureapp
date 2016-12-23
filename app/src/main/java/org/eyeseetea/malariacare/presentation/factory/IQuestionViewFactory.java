package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;

import org.eyeseetea.malariacare.views.question.IQuestionView;

public interface IQuestionViewFactory {
    IQuestionView getView(Context context, int typeQuestion);

/*    AKeyboardQuestionView.onAnswerChangedListener getStringAnswerChangedListener(
            TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter);

    AOptionQuestionView.onAnswerChangedListener getOptionAnswerChangedListener(
            TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter);*/
}
