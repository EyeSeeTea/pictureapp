package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.INavigationQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class ReminderSingleQuestionView extends AOptionQuestionView implements IQuestionView,
        IImageQuestionView, INavigationQuestionView {

    OptionDB navigationOption;

    public ReminderSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> options) {
        setupTextOption(options.get(0));

        navigationOption = options.get(1);
    }

    public void setQuestionDB(QuestionDB questionDB) {

    }

    @Override
    public void setImage(String path) {
        ReminderSingleQuestionViewHelper.setImage(this, path);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(ValueDB value) {
    }

    private void init(Context context) {
        inflate(context, R.layout.dynamic_tab_reminder_row, this);
    }

    private void setupTextOption(OptionDB option) {
        TextView title = (TextView) findViewById(R.id.questionTextRow);
        title.setText(option.getInternationalizedName());
        title.setTextSize(option.getOptionAttributeDB().getText_size());

        TextView subTitle = (TextView) findViewById(R.id.questionSubText);
        subTitle.setText(option.getInternationalizedCode());
        subTitle.setTextSize(option.getOptionAttributeDB().getText_size());
    }

    @Override
    public String nextText() {
        if (navigationOption == null) {
            return "";
        }

        return navigationOption.getInternationalizedName();
    }

    @Override
    public int nextTextSize() {
        if (navigationOption == null) {
            return 0;
        }

        return navigationOption.getOptionAttributeDB().getText_size();
    }

    public void notifyAnswerChanged(OptionDB option) {
    }
}
