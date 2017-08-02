package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.INavigationQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class ReminderSingleQuestionView extends AOptionQuestionView implements IQuestionView,
        IImageQuestionView, INavigationQuestionView {

    OptionDB mNavigationOptionDB;

    public ReminderSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        setupTextOption(optionDBs.get(0));

        mNavigationOptionDB = optionDBs.get(1);
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
    public void setValue(Value value) {
    }

    private void init(Context context) {
        inflate(context, R.layout.dynamic_tab_reminder_row, this);
    }

    private void setupTextOption(OptionDB optionDB) {
        TextView title = (TextView) findViewById(R.id.questionTextRow);
        title.setText(optionDB.getInternationalizedCode());
        title.setTextSize(optionDB.getOptionAttributeDB().getText_size());

        TextView subTitle = (TextView) findViewById(R.id.questionSubText);
        subTitle.setText(optionDB.getInternationalizedName());
        subTitle.setTextSize(optionDB.getOptionAttributeDB().getText_size());
    }

    @Override
    public String nextText() {
        if (mNavigationOptionDB == null) {
            return "";
        }

        return mNavigationOptionDB.getInternationalizedCode();
    }

    @Override
    public int nextTextSize() {
        if (mNavigationOptionDB == null) {
            return 0;
        }

        return mNavigationOptionDB.getOptionAttributeDB().getText_size();
    }

    public void notifyAnswerChanged(OptionDB optionDB) {
    }
}
