package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.utils.Constants;


public class DropdownMultiQuestionViewStrategy extends ADropdownMultiQuestionViewStrategy {

    public DropdownMultiQuestionViewStrategy(Context context) {
        super(context);
    }

    @Override
    public OptionDB getDefaultOption(QuestionDB questionDB) {
        if (questionDB.getOutput() == Constants.DROPDOWN_OU_LIST) {
            return new OptionDB(mContext.getString(R.string.village), null, 0f, null);
        } else {
            return super.getDefaultOption(questionDB);
        }
    }
}
