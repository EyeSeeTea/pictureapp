package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Question {
    private String mUid;
    private String mQuestionText;

    public Question(String uid, String questionText) {
        this.mUid = required(uid, "uid is required");
        this.mQuestionText = required(questionText, "questionText is required");
    }

    public String getUid() {
        return mUid;
    }

    public String getQuestionText() {
        return mQuestionText;
    }
}
