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
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    final String TITLE_SEPARATOR = ": ";

    public TableRow createViewRow(TableRow rowView, Value value) {

        rowView.setTag(getCorrectQuestion(value.getQuestionUId()));

        //Sets the value text in the row and add the question as tag.
        CustomTextView questionTextView = (CustomTextView) rowView.findViewById(
                R.id.review_title_text);


        questionTextView.setText(questionTextView.getText().toString() +
                ((value.getInternationalizedName() != null) ? value.getInternationalizedName()
                        : value.getValue()));
        if ((value.getQuestionUId() != null)) {
            questionTextView.setText(
                    Question.findByUID(value.getQuestionUId()).getInternationalizedCodeDe_Name() + TITLE_SEPARATOR);
            //Adds click listener to hide the fragment and go to the clicked question.
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!DynamicTabAdapter.isClicked) {
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
        if (questionUId.equals(PreferencesState.getInstance().getContext().getString(
                R.string.dynamicTreatmentQuestionUID)) || questionUId.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.referralQuestionUID))
                || questionUId.equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.treatmentDiagnosisVisibleQuestion))) {
            return Question.findByUID(PreferencesState.getInstance().getContext().getString(
                    R.string.dynamicTreatmentHideQuestionUID));
        }
        if (questionUId.equals(PreferencesState.getInstance().getContext().getString(
                R.string.outOfStockQuestionUID))) {
            return Question.findByUID(PreferencesState.getInstance().getContext().getString(
                    R.string.dynamicStockQuestionUID));
        }
        return Question.findByUID(questionUId);
    }

    public static boolean isValidValue(Value value) {
        if (Session.getStockSurvey()==null || value.getQuestionUId() == null) {
            return false;
        }
        for (org.eyeseetea.malariacare.data.database.model.Value stockValue : Session.getStockSurvey().getValuesFromDB()) {
            if (stockValue.getQuestion() != null) {
                if (stockValue.getQuestion().getUid().equals(value.getQuestionUId())) {
                    return true;
                }
            }
        }
        return false;
    }

}
