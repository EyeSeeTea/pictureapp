package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    public TableRow createViewRow(TableRow rowView, Value value) {
        //Sets the value text in the row and add the question as tag.
        CustomTextView textCard = (CustomTextView) rowView.findViewById(R.id.review_content_text);
        textCard.setText((value.getInternationalizedName() != null) ? value.getInternationalizedName()
                : value.getValue());
        if (textCard.getText().equals("")) {
            textCard.setText(PreferencesState.getInstance().getContext().getString(
                    R.string.empty_review_value));
        }
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

    public static boolean shouldShowReviewScreen(){
        return true;
    }

    @Override
    public List<org.eyeseetea.malariacare.data.database.model.Value> orderValues(
            List<org.eyeseetea.malariacare.data.database.model.Value> values) {
        List<org.eyeseetea.malariacare.data.database.model.Value> orderedList = new ArrayList<>();
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        Question nextQuestion = null;
        do {
            for (org.eyeseetea.malariacare.data.database.model.Value value : values) {
                if (value.getQuestion() != null) {
                    if (value.getQuestion().equals(navigationController.getCurrentQuestion())) {
                        orderedList.add(value);
                        nextQuestion = navigationController.next(value.getOption());
                    }
                }
            }
        } while (nextQuestion != null);
        return orderedList;
    }
}
