package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;
import android.text.InputType;

import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.DropdownMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.LabelMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.NumberMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PhoneMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PositiveNumberMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PositiveOrZeroNumberMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.PregnantMonthNumberMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.RadioButtonMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.SwitchMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.TextMultiQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.YearSelectorQuestionView;

public class MultiQuestionViewFactory implements IQuestionViewFactory {
    public IQuestionView getView(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.SHORT_TEXT:
                TextMultiQuestionView shortTextMultiQuestionView = new TextMultiQuestionView(
                        context);
                shortTextMultiQuestionView.setInputType(
                        InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                return shortTextMultiQuestionView;
            case Constants.LONG_TEXT:
                TextMultiQuestionView longTextMultiQuestionView = new TextMultiQuestionView(
                        context);
                longTextMultiQuestionView.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                return longTextMultiQuestionView;
            case Constants.PHONE:
                return new PhoneMultiQuestionView(context);
            case Constants.INT:
                return new NumberMultiQuestionView(context);
            case Constants.POSITIVE_INT:
                return new PositiveNumberMultiQuestionView(context);
            case Constants.POSITIVE_OR_ZERO_INT:
                return new PositiveOrZeroNumberMultiQuestionView(context);
            case Constants.PREGNANT_MONTH_INT:
                return new PregnantMonthNumberMultiQuestionView(context);
            case Constants.RADIO_GROUP_HORIZONTAL:
                return new RadioButtonMultiQuestionView(context);
            case Constants.QUESTION_LABEL:
                return new LabelMultiQuestionView(context);
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                return new DropdownMultiQuestionView(context);
            case Constants.SWITCH_BUTTON:
                return new SwitchMultiQuestionView(context);
            case Constants.YEAR:
                return new YearSelectorQuestionView(context);
            default:
                return MultiQuestionViewFactoryStrategy.createQuestion(context, typeQuestion);

        }
    }
}
