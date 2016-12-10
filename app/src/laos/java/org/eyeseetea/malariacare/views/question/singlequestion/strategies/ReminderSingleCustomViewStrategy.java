package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;

public class ReminderSingleCustomViewStrategy implements IReminderSingleCustomViewStrategy {
    DynamicTabAdapter mDynamicTabAdapter;

    public ReminderSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void initWarningText(View rootView, Option option) {
        mDynamicTabAdapter.initWarningText(rootView, option);
    }

    public void initWarningValue(View rootView, Option option) {
        mDynamicTabAdapter.initWarningValue(rootView, option);
    }
}
