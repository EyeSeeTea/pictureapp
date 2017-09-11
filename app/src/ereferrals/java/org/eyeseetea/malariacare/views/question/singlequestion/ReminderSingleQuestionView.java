package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.INavigationQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class ReminderSingleQuestionView extends AOptionQuestionView implements IQuestionView,
        IImageQuestionView, INavigationQuestionView {

    Option navigationOption;

    public ReminderSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<Option> options) {
        setupTextOption(options.get(0));

        navigationOption = options.get(1);
    }

    @Override
    public void setQuestion(Question question) {

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

    private void setupTextOption(Option option) {
        TextView title = (TextView) findViewById(R.id.questionTextRow);
        title.setText(option.getInternationalizedCode());
        title.setTextSize(option.getOptionAttribute().getText_size());

        TextView subTitle = (TextView) findViewById(R.id.questionSubText);
        subTitle.setText(option.getInternationalizedName());
        subTitle.setTextSize(option.getOptionAttribute().getText_size());
    }

    @Override
    public String nextText() {
        if (navigationOption == null) {
            return "";
        }

        return navigationOption.getInternationalizedCode();
    }

    @Override
    public int nextTextSize() {
        if (navigationOption == null) {
            return 0;
        }

        return navigationOption.getOptionAttribute().getText_size();
    }

    public void notifyAnswerChanged(Option option) {
    }
}
