package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

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

    final String TITLE_SEPARATOR = ":";

    public TableRow createViewRow(TableRow rowView, Value value, int position) {
        //Sets the value text in the row and add the question as tag.
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);
        CustomTextView answerText = (CustomTextView) rowView.findViewById(R.id.review_answer);

        if ((value.getQuestionUId() != null)) {
            rowView.setTag(value.getQuestionUId());

            String question = (QuestionDB.findByUID(
                    value.getQuestionUId()).getInternationalizedCodeDe_Name());

            if (!question.contains(TITLE_SEPARATOR)) {
                question = question + TITLE_SEPARATOR;
            }

            String answer =
                    ((value.getInternationalizedName() != null) ? value.getInternationalizedName()
                            : value.getValue());

            if (answer.equals("")) {
                answer = (PreferencesState.getInstance().getContext().getString(
                        R.string.empty_review_value));
            }

            questionTextView.setText(question);
            answerText.setText(answer);


            //Adds click listener to hide the fragment and go to the clicked question.
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!DynamicTabAdapter.isClicked) {
                        DynamicTabAdapter.isClicked = true;
                        String questionUId = (String) v.getTag();
                        onClickListener.onClickOnValue(questionUId);
                    }
                }
            });

            rowView.setBackgroundColor(
                    Color.parseColor(value.getBackgroundColor()));

        }
        return rowView;
    }

    public static boolean shouldShowReviewScreen() {
        return true;
    }

}
