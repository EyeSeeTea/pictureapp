package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;

public class NumberSingleQuestionViewStrategyStrategy extends
        ANumberSingleQuestionViewStrategy {
    @Override
    public void setQuestionDB(AKeyboardSingleQuestionView singleQuestionView,
            QuestionDB questionDB) {
        TextView question = (TextView) singleQuestionView.findViewById(R.id.question);
        question.setText(questionDB.getInternationalizedForm_name());
    }
}
