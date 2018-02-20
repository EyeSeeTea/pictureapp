package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.views.question.singlequestion.PositiveNumberSingleQuestionView;

public class PositiveNumberSingleQuestionViewStrategyStrategy extends
        APositiveNumberSingleQuestionViewStrategy {
    @Override
    public void setQuestionDB(PositiveNumberSingleQuestionView positiveNumberSingleQuestionView,
            QuestionDB questionDB) {
        TextView question = (TextView) positiveNumberSingleQuestionView.findViewById(R.id.question);
        question.setText(questionDB.getInternationalizedForm_name());
    }
}
