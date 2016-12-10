package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.database.model.Option;

public interface IReminderSingleCustomViewStrategy {
    void initWarningText(View rootView, Option option);

    void initWarningValue(View rootView, Option option);
}
