package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public interface IConfirmCounterSingleCustomViewStrategy {
    void showConfirmCounter(final View view, final OptionDB selectedOptionDB,
            QuestionDB questionDB, QuestionDB questionDBCounter);
}
