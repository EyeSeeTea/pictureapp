package org.eyeseetea.malariacare.strategies;

import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    final String TITLE_SEPARATOR = ": ";

    public TableRow createViewRow(TableRow rowView, Value value) {

        rowView.setTag(getCorrectQuestion(value.getQuestion()));

        //Sets the value text in the row and add the question as tag.
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);

        if ((value.getQuestion() != null)) {
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

            questionTextView.setText(questionTextView.getText().toString() +
                    ((value.getOption() != null) ? value.getOption().getInternationalizedCode()
                            : value.getValue()));

            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                rowView.setBackgroundColor(
                        Color.parseColor(
                                "#" + value.getOption().getBackground_colour()));
            }

        }
        return rowView;
    }


    private Question getCorrectQuestion(Question question) {
        if (question.getUid().equals(PreferencesState.getInstance().getContext().getString(
                R.string.dynamicTreatmentQuestionUID)) || question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.referralQuestionUID))
                || question.getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.treatmentDiagnosisVisibleQuestion))) {
            return Question.findByUID(PreferencesState.getInstance().getContext().getString(
                    R.string.dynamicTreatmentHideQuestionUID));
        }
        if (question.getUid().equals(PreferencesState.getInstance().getContext().getString(
                R.string.outOfStockQuestionUID))) {
            return Question.findByUID(PreferencesState.getInstance().getContext().getString(
                    R.string.dynamicStockQuestionUID));
        }
        return question;
    }

    public static boolean isValidValue(Value value) {
        if (Session.getStockSurvey()==null || value.getQuestion() == null) {
            return false;
        }
        for (Value stockValue : Session.getStockSurvey().getValuesFromDB()) {
            if (stockValue.getQuestion() != null) {
                if (stockValue.getQuestion().getUid().equals(value.getQuestion().getUid())) {
                    return true;
                }
            }
        }
        return false;
    }
}
