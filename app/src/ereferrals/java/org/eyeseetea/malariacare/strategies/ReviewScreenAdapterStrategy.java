
package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewScreenAdapterStrategy extends AReviewScreenAdapterStrategy {
    ReviewScreenAdapter.onClickListener onClickListener;

    public ReviewScreenAdapterStrategy(ReviewScreenAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    final String TITLE_SEPARATOR = ": ";

    @Override
    public TableRow createViewRow(TableRow rowView, Value value, int position) {
        Context context = PreferencesState.getInstance().getContext();

        rowView.setTag(value.getQuestionUId());

        //Sets the value text in the row and add the question as tag.
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);
        TextView answerText = (TextView) rowView.findViewById(R.id.review_answer);

        if ((value.getQuestionUId() != null)) {
            String question = (QuestionDB.findByUID(
                    value.getQuestionUId()).getInternationalizedCodeDe_Name() + TITLE_SEPARATOR);
            String answer =
                    ((value.getInternationalizedCode() != null) ? value.getInternationalizedCode()
                            : value.getValue());
            questionTextView.setText(question);
            answerText.setText(answer);
            //Adds click listener to hide the fragment and go to the clicked question.
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DynamicTabAdapter.isClicked) {
                        DynamicTabAdapter.isClicked = true;
                        String questionUId = (String) v.getTag();
                        onClickListener.onClickOnValue(questionUId);
                        DashboardActivity.dashboardActivity.hideReview(questionUId);
                    }
                }
            });

            rowView.setBackgroundColor(
                    position % 2 != 0 ? context.getResources().getColor(R.color.light_grey) :
                            context.getResources().getColor(R.color.white));
        }
        return rowView;
    }

    private QuestionDB getCorrectQuestion(String questionUId) {
        return QuestionDB.findByUID(questionUId);
    }


    public static boolean isValidValue(Value value) {
        if (value.getQuestionUId() == null) {
            return false;
        }

        return true;
    }

}