package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;

import java.util.List;

public class ReminderSingleCustomViewStrategy implements IReminderSingleCustomViewStrategy {

    DynamicTabAdapter mDynamicTabAdapter;

    public ReminderSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        mDynamicTabAdapter = dynamicTabAdapter;
    }

    @Override
    public void showQuestionInfo(View rootView, Question question) {
        List<Option> questionOptions = question.getAnswer().getOptions();

        //Question "header" is in the first option in Options.csv
        if (questionOptions != null && questionOptions.size() > 0) {
            mDynamicTabAdapter.initWarningText(rootView, questionOptions.get(0));
        }
        //Question "button" is in the second option in Options.csv
        if (questionOptions != null && questionOptions.size() > 1) {
            mDynamicTabAdapter.initWarningValue(rootView, questionOptions.get(1));
        }
    }

    @Override
    public void showAndHideViews(View rootView) {
        //Show confirm on full screen
        rootView.findViewById(R.id.scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.no_container).setVisibility(View.GONE);
    }
}
