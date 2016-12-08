package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.listeners.question.MultiQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PhoneMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PositiveNumberMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.RadioButtonMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.ShortTextMultiQuestionView;

public class MultiQuestionViewFactory implements IQuestionViewFactory {
    public IQuestionView getView(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.SHORT_TEXT:
                return new ShortTextMultiQuestionView(context);
            case Constants.PHONE:
                return new PhoneMultiQuestionView(context);
            case Constants.POSITIVE_INT:
                return new PositiveNumberMultiQuestionView(context);
            case Constants.RADIO_GROUP_HORIZONTAL:
                return new RadioButtonMultiQuestionView(context);
        }

        throw new IllegalArgumentException("Not exists any question for type " + typeQuestion);
    }

    @Override
    public AKeyboardQuestionView.onAnswerChangedListener getStringAnswerChangedListener(
            TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter) {
        return new MultiQuestionAnswerChangedListener(tableLayout);
    }

    @Override
    public AOptionQuestionView.onAnswerChangedListener getOptionAnswerChangedListener(
            TableLayout tableLayout, DynamicTabAdapter dynamicTabAdapter) {
        return new MultiQuestionAnswerChangedListener(tableLayout);
    }
}
