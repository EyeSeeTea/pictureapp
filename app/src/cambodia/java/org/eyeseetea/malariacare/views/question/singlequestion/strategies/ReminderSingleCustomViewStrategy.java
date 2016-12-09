package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.TextCard;

public class ReminderSingleCustomViewStrategy {

    DynamicTabAdapter mDynamicTabAdapter;

    public ReminderSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void initWarningText(View rootView, Option option) {
        mDynamicTabAdapter.initWarningText(rootView,option);
    }

    public void initWarningValue(View rootView, Option option) {
        mDynamicTabAdapter.initWarningValue(rootView,option);
    }
}
