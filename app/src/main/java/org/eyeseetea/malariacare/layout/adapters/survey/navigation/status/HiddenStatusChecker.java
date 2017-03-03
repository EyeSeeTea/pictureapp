package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.Question;

public class HiddenStatusChecker extends StatusChecker {

    private Question hiddenQuestion;

    public HiddenStatusChecker(Question hiddenQuestion) {
        this.hiddenQuestion = hiddenQuestion;
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
