package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public abstract class ATextSingleQuestionViewStrategy {

    public abstract void setQuestionDB(
            View view,
            QuestionDB questionDB);

}
