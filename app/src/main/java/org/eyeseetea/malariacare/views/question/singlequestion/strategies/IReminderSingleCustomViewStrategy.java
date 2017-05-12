package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Question;

public interface IReminderSingleCustomViewStrategy {
    void showQuestionInfo(View rootView, Question question);

    void showAndHideViews(View rootView);
}
