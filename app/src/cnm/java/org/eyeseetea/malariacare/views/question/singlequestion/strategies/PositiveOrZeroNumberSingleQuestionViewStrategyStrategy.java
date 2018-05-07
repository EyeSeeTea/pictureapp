package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public class PositiveOrZeroNumberSingleQuestionViewStrategyStrategy extends
        APositiveOrZeroNumberSingleQuestionViewStrategy {
    @Override
    public void setQuestionDB(View view, QuestionDB questionDB) {
        TextView textView = (TextView) view.findViewById(R.id.question);
        textView.setText(questionDB.getInternationalizedForm_name());
    }
}
