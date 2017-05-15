package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {


    public TableRow createViewRow(TableRow rowView, Value value) {
        //Sets the value text in the row and add the question as tag.
        CustomTextView textCard = (CustomTextView) rowView.findViewById(R.id.review_content_text);
        textCard.setText((value.getInternationalizedCode() != null) ? value.getInternationalizedCode()
                : value.getValue());
        if ((value.getQuestionUId() != null)) {
            textCard.setTag(value.getQuestionUId());

            //Adds click listener to hide the fragment and go to the clicked question.
            textCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DynamicTabAdapter.isClicked) {
                        DynamicTabAdapter.isClicked = true;
                        String questionUId = (String) v.getTag();

                        DashboardActivity.dashboardActivity.hideReview(questionUId);
                    }
                }
            });
            textCard.setBackgroundColor(
                    Color.parseColor(value.getBackgroundColor()));

        }
        return rowView;
    }
}
