package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.layout.listeners.question.AQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.views.question.AQuestionView;

public interface IQuestionViewFactory {
    AQuestionView getView(Context context, int typeQuestion);

    AQuestionView.onAnswerChangedListener getAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter);
}
