package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;

public interface IConfirmCounterSingleCustomViewStrategy {
    void showConfirmCounter(final View view, final Option selectedOption,
            Question question, Question questionCounter);
}
