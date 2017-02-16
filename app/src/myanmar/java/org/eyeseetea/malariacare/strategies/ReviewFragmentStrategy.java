package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    final String TITLE_SEPARATOR = ": ";

    public TableRow createViewRow(TableRow rowView, Value value) {
        //Sets the value text in the row and add the question as tag.
        CustomTextView valueTextView = (CustomTextView) rowView.findViewById(
                R.id.review_content_text);
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);
        valueTextView.setText(
                (value.getOption() != null) ? value.getOption().getInternationalizedCode()
                        : value.getValue());
        if ((value.getQuestion() != null)) {
            valueTextView.setTag(value.getQuestion());
            questionTextView.setText(
                    value.getQuestion().getInternationalizedCodeDe_Name() + TITLE_SEPARATOR);
            //Adds click listener to hide the fragment and go to the clicked question.
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = (Question) v.getTag();
                    DashboardActivity.dashboardActivity.hideReview(question);
                }
            });

            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                rowView.setBackgroundColor(
                        Color.parseColor(
                                "#" + value.getOption().getBackground_colour()));
            }

        }
        return rowView;
    }
}
