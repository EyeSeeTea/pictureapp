package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    public TableRow createViewRow(TableRow rowView, Value value) {
        //Sets the value text in the row and add the question as tag.
        CustomTextView textCard = (CustomTextView) rowView.findViewById(R.id.review_content_text);
        textCard.setText((value.getOption() != null) ? value.getOption().getInternationalizedCode()
                : value.getValue());
        if ((value.getQuestion() != null)) {
            textCard.setTag(value.getQuestion());

            //Adds click listener to hide the fragment and go to the clicked question.
            textCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DynamicTabAdapter.isClicked) {
                        DynamicTabAdapter.isClicked = true;
                        Question question = (Question) v.getTag();
                        DashboardActivity.dashboardActivity.hideReview(question);
                    }
                }
            });

            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                textCard.setBackgroundColor(
                        Color.parseColor("#" + value.getOption().getBackground_colour()));
            }

        }
        return rowView;
    }

    public static boolean isValidValue(Value value) {
        if (value.getQuestion() == null) {
            return false;
        }
        return true;
    }
}
