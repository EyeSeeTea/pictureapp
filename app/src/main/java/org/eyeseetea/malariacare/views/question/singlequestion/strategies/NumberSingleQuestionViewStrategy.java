package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;

public abstract class NumberSingleQuestionViewStrategy {

    public abstract void setQuestionDB(
            AKeyboardSingleQuestionView keyboardSingleQuestionView,
            QuestionDB questionDB);
}
