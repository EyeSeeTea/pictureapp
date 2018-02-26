package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.views.question.singlequestion.PositiveNumberSingleQuestionView;

public abstract class APositiveNumberSingleQuestionViewStrategy {

    public abstract void setQuestionDB(
            PositiveNumberSingleQuestionView positiveNumberSingleQuestionView,
            QuestionDB questionDB);
}
