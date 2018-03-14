package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public class TextSingleQuestionViewStrategy extends ATextSingleQuestionViewStrategy {
    @Override
    public void setQuestionDB(View view,
            QuestionDB questionDB) {
        TextView question = (TextView) view.findViewById(R.id.question);
        question.setText(questionDB.getInternationalizedForm_name());
    }
}
