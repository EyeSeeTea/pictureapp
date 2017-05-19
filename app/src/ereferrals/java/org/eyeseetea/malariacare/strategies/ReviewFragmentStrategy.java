
package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    final String TITLE_SEPARATOR = ": ";

    @Override
    public TableRow createViewRow(TableRow rowView, Value value) {

        rowView.setTag(value.getQuestionUId());

        //Sets the value text in the row and add the question as tag.
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);

        if ((value.getQuestionUId() != null)) {
            String rowText = (Question.findByUID(
                    value.getQuestionUId()).getInternationalizedCodeDe_Name() + TITLE_SEPARATOR)
                    + ((value.getInternationalizedCode() != null) ? value.getInternationalizedCode()
                    : value.getValue());

            questionTextView.setText(rowText);
            //Adds click listener to hide the fragment and go to the clicked question.
            questionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DynamicTabAdapter.isClicked) {
                        DynamicTabAdapter.isClicked = true;
                        String questionUId = (String) v.getTag();

                        DashboardActivity.dashboardActivity.hideReview(questionUId);
                    }
                }
            });

            questionTextView.setBackgroundColor(
                    Color.parseColor(value.getBackgroundColor()));
        }
        return rowView;
    }

    private Question getCorrectQuestion(String questionUId) {
        return Question.findByUID(questionUId);
    }


    public static boolean isValidValue(Value value) {
        if (value.getQuestionUId() == null) {
            return false;
        }

        return true;
    }

}