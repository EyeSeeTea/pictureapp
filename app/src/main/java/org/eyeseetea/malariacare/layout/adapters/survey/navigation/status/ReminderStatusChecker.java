package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Checker that helps to decide if a node is visible or not according to the current survey values.
 * This policy will be attached to those nodes with a Reminder relationship
 * Created by arrizabalaga on 13/07/16.
 */
public class ReminderStatusChecker extends StatusChecker {

    /**
     * List of QuestionOptions that will enable this checker
     */
    List<QuestionOptionDB> reminderTriggers;

    public ReminderStatusChecker(QuestionDB reminderQuestionDB) {
        initRemainderTriggers(reminderQuestionDB);
    }

    @Override
    public boolean isEnabled() {
        for (QuestionOptionDB questionOptionDB : reminderTriggers) {
            if (isSelected(questionOptionDB)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleInReview() {
        return false;
    }

    private void initRemainderTriggers(QuestionDB reminderQuestionDB) {
        this.reminderTriggers = new ArrayList<>();

        //Look for a REMINDER relation which origin mQuestionOptionDB is activated
        for (QuestionRelationDB questionRelationDB : reminderQuestionDB.getQuestionRelationDBs()) {
            if (!questionRelationDB.isAReminder()) {
                continue;
            }

            //Find QuestionOptionDB for this relation
            QuestionOptionDB questionOptionDB = findQuestionOption(questionRelationDB);
            if (questionOptionDB == null) {
                continue;
            }

            //Annotate questionOptionDB to check
            this.reminderTriggers.add(questionOptionDB);
        }
    }

    /**
     * Checks if the given questionOptionDB is activated
     */
    private boolean isSelected(QuestionOptionDB questionOptionDB) {
        OptionDB currentOptionDB = questionOptionDB.getQuestionDB().getOptionByValueInSession();
        return currentOptionDB != null
                && currentOptionDB.getId_option() == questionOptionDB.getOptionDB().getId_option();
    }


}
