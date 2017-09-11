package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;

import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion
        .DynamicStockImageRadioButtonSingleQuestionView;

class SingleQuestionViewFactoryStrategy {

    public static IQuestionView createQuestion(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON:
                return new DynamicStockImageRadioButtonSingleQuestionView(context);
            default:
                return null;
        }

    }
}
