package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

public interface IQuestionViewFactory {
    IQuestionView getView(Context context, int typeQuestion);

    AKeyboardQuestionView.onAnswerChangedListener getStringAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter);

    AOptionQuestionView.onAnswerChangedListener getOptionAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter);
}
