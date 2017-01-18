package org.eyeseetea.malariacare.domain.usecase;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.views.TextCard;

/**
 * Created by idelcano on 15/12/2016.
 */

public class ReviewUseCase {

    public static View createViewRow(ViewGroup rowView, Value value) {
        //Sets the value text in the row and add the question as tag.
        TextCard textCard = (TextCard) rowView.findViewById(R.id.review_content_text);
        textCard.setText((value.getOption() != null) ? value.getOption().getInternationalizedCode()
                : value.getValue());
        if ((value.getQuestion() != null)) {
            textCard.setTag(value.getQuestion());

            //Adds click listener to hide the fragment and go to the clicked question.
            textCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = (Question) v.getTag();
                    DashboardActivity.dashboardActivity.hideReview(question);
                }
            });

            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                textCard.setBackgroundColor(
                        Color.parseColor("#" + value.getOption().getBackground_colour()));
            }

        }
        return rowView;
    }
}
