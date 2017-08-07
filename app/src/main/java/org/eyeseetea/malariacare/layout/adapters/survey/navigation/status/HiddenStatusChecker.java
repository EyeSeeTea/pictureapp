package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;

public class HiddenStatusChecker extends StatusChecker {

    private QuestionDB mHiddenQuestionDB;

    public HiddenStatusChecker(QuestionDB hiddenQuestionDB) {
        this.mHiddenQuestionDB = hiddenQuestionDB;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isVisibleInReview() {
        return false;
    }
}
