package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class ReminderSingleCustomViewStrategy implements IReminderSingleCustomViewStrategy {
    public ReminderSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {

    }

    public void showQuestionInfo(View rootView, Question question) {
        List<Option> questionOptions = question.getAnswer().getOptions();

        showImage(rootView, question);

        //Question "header" is in the first option in Options.csv
        if (questionOptions != null && questionOptions.size() > 0) {
            showText(rootView, questionOptions.get(0));
        }
        //Question "button" is in the second option in Options.csv
        if (questionOptions != null && questionOptions.size() > 1) {
            configureNextButton(rootView, questionOptions.get(1));
        }
    }

    private void showImage(View rootView, Question question) {
        if (question.getPath() != null && !question.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            BaseLayoutUtils.putImageInImageViewDensityHight(question.getInternationalizedPath(),
                    imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void showText(View rootView, Option option) {
        TextView title = (TextView) rootView.findViewById(R.id.questionTextRow);
        title.setText(option.getInternationalizedCode());
        title.setTextSize(option.getOptionAttribute().getText_size());

        TextView subTitle = (TextView) rootView.findViewById(R.id.questionSubText);
        subTitle.setText(option.getInternationalizedName());
        subTitle.setTextSize(option.getOptionAttribute().getText_size());
    }

    private void configureNextButton(View rootView, Option option) {
        CustomTextView textNextButton = (CustomTextView) rootView.findViewById(R.id.next_txt);
        textNextButton.setText(option.getInternationalizedCode());
        textNextButton.setTextSize(option.getOptionAttribute().getText_size());
    }

    public void showAndHideViews(View rootView) {
        //Show confirm on full screen
        rootView.findViewById(R.id.scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);
    }
}
