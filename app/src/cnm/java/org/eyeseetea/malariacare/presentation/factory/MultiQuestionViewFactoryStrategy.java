package org.eyeseetea.malariacare.presentation.factory;

import android.content.Context;

import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.OuTreeMultiQuestionView;

public class MultiQuestionViewFactoryStrategy {
    public static IQuestionView createQuestion(Context context, int typeQuestion) {
        if (typeQuestion == Constants.DROPDOWN_LIST_OU_TREE) {
            return new OuTreeMultiQuestionView(context);
        }
        return null;
    }
}
