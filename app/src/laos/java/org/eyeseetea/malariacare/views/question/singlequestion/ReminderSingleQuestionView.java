package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class ReminderSingleQuestionView extends AOptionQuestionView implements IQuestionView,
        IImageQuestionView {

    public ReminderSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> options) {

        //Question "header" is in the first option in Options.csv
        if (options != null && options.size() > 0) {
            ReminderSingleQuestionViewHelper.setWarningText(this, options.get(0));
        }
        //Question "button" is in the second option in Options.csv
        if (options != null && options.size() > 1) {
            ReminderSingleQuestionViewHelper.setWarningValue(this, options.get(1), this);
        }
    }


    @Override
    public void setQuestionDB(QuestionDB question) {

    }

    @Override
    public void setImage(String path) {
        ReminderSingleQuestionViewHelper.setImage(this, path);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(ValueDB valueDB) {
    }

    private void init(Context context) {

        inflate(context, R.layout.dynamic_tab_reminder_row, this);
    }

    public void notifyAnswerChanged(OptionDB option) {
        super.notifyAnswerChanged(option);
    }
}
