package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.TextCard;

public class ReminderSingleCustomViewStrategy implements IReminderSingleCustomViewStrategy {
    public ReminderSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {

    }

    public void initWarningText(View rootView, Option option) {
        TextView title = (TextView) rootView.findViewById(R.id.questionTextRow);
        title.setText(option.getInternationalizedCode());
        title.setTextSize(option.getOptionAttribute().getText_size());

        TextView subTitle = (TextView) rootView.findViewById(R.id.questionSubText);
        subTitle.setText(option.getInternationalizedName());
        subTitle.setTextSize(option.getOptionAttribute().getText_size());
    }

    public void initWarningValue(View rootView, Option option) {
        hideStandardButtons(rootView);

        TextCard textNextButton = (TextCard) rootView.findViewById(R.id.next_txt);
        textNextButton.setText(option.getInternationalizedCode());
        textNextButton.setTextSize(option.getOptionAttribute().getText_size());
    }

    private void hideStandardButtons(View rootView) {
        TextCard standardTextYes = (TextCard) rootView.findViewById(R.id.textcard_confirm_yes);
        ImageView standardButtonYes = (ImageView) rootView.findViewById(R.id.confirm_yes);
        standardTextYes.setVisibility(View.GONE);
        standardButtonYes.setVisibility(View.GONE);
        TextCard standardTextNo = (TextCard) rootView.findViewById(R.id.textcard_confirm_no);
        ImageView standardButtonNo = (ImageView) rootView.findViewById(R.id.confirm_no);
        standardTextNo.setVisibility(View.GONE);
        standardButtonNo.setVisibility(View.GONE);
    }
}
