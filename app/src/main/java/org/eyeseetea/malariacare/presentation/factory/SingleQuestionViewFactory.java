package org.eyeseetea.malariacare.presentation.factory;


import android.content.Context;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.listeners.question.SingleQuestionAnswerChangedListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.ImageOptionSingleQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.ImageRadioButtonSingleQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.PhoneSingleQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.PositiveNumberSingleQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.ReminderSingleQuestionView;


public class SingleQuestionViewFactory implements IQuestionViewFactory {
    public IQuestionView getView(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.PHONE:
                return new PhoneSingleQuestionView(context);
            case Constants.POSITIVE_INT:
                return new PositiveNumberSingleQuestionView(context);
            case Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT:
                return new ImageRadioButtonSingleQuestionView(context);
            case Constants.IMAGE_RADIO_GROUP:
                return new ImageRadioButtonSingleQuestionView(context);
            case Constants.REMINDER:
            case Constants.WARNING:
                return new ReminderSingleQuestionView(context);
            case Constants.IMAGES_2:
            case Constants.IMAGES_4:
            case Constants.IMAGES_6:
                return new ImageOptionSingleQuestionView(context);
        }

        throw new IllegalArgumentException("Not exists any question for type " + typeQuestion);
    }

    @Override
    public AKeyboardQuestionView.onAnswerChangedListener getStringAnswerChangedListener(
            TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter) {
        return new SingleQuestionAnswerChangedListener(tableLayout, dynamicTabAdapter);
    }

    @Override
    public AOptionQuestionView.onAnswerChangedListener getOptionAnswerChangedListener(
            TableLayout tableLayout, DynamicTabAdapter dynamicTabAdapter) {
        return new SingleQuestionAnswerChangedListener(tableLayout, dynamicTabAdapter);
    }
}
