package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;

import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.NumberRadioButtonMultiquestionView;

public class MultiQuestionViewFactoryStrategy {
    public static IQuestionView createQuestion(Context context, int typeQuestion) {
        switch (typeQuestion) {
            case Constants.DYNAMIC_TREATMENT_SWITCH_NUMBER:
                return new NumberRadioButtonMultiquestionView(context);
            default:
                return null;
        }
    }
}
