package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;

import java.util.List;

/**
 * Base class that helps a QuestionNode to decide whether isEnabled (visitable) or
 * can be seen during a previous/review action.
 * Created by arrizabalaga on 13/07/16.
 */
public class StatusChecker {
    /**
     * By default every node is enabled
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * By default every node is visible during review
     */
    public boolean isVisibleInReview() {
        return true;
    }

    /**
     * Navigates from questionRelationDB -> match -> mQuestionOptionDB
     */
    protected QuestionOptionDB findQuestionOption(QuestionRelationDB questionRelationDB) {
        if (questionRelationDB == null) {
            return null;
        }
        List<QuestionOptionDB> questionOptionDBs = QuestionOptionDB.findByQuestionRelation(
                questionRelationDB);
        if (questionOptionDBs.isEmpty()) {
            return null;
        }

        return questionOptionDBs.get(0);
    }
}
