package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public interface IReminderSingleCustomViewStrategy {
    void showQuestionInfo(View rootView, QuestionDB questionDB);

    void showAndHideViews(View rootView);
}
