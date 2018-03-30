package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.utils.Constants;

public abstract class ADropdownMultiQuestionViewStrategy {
    protected Context mContext;

    public ADropdownMultiQuestionViewStrategy(Context context) {
        mContext = context;
    }

    public OptionDB getDefaultOption(QuestionDB questionDB) {
        return new OptionDB(Constants.DEFAULT_SELECT_OPTION);
    }

}
