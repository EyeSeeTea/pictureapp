package org.eyeseetea.malariacare.presentation.factory;


import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.layout.listeners.question.AQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.layout.listeners.question.MultiQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AQuestionView;
import org.eyeseetea.malariacare.views.question.PhoneMultiQuestionView;
import org.eyeseetea.malariacare.views.question.ShortTextMultiQuestionView;

public class MultiQuestionViewFactory implements IQuestionViewFactory {
    public AQuestionView getView(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.SHORT_TEXT:
                return new ShortTextMultiQuestionView(context);
            case Constants.PHONE:
                return new PhoneMultiQuestionView(context);
        }

        throw new IllegalArgumentException("Not exists any question for type " + typeQuestion);
    }

    @Override
    public AQuestionView.onAnswerChangedListener getAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter) {
        return new MultiQuestionAnswerChangedListener(tableLayout);
    }
}
