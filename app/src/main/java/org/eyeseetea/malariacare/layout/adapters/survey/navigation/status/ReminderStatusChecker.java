package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;

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
    List<QuestionOption> reminderTriggers;

    public ReminderStatusChecker(Question reminderQuestion) {
        initRemainderTriggers(reminderQuestion);
    }

    @Override
    public boolean isEnabled() {
        for (QuestionOption questionOption : reminderTriggers) {
            if (isSelected(questionOption)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleInReview() {
        return false;
    }

    private void initRemainderTriggers(Question reminderQuestion) {
        this.reminderTriggers = new ArrayList<>();

        //Look for a REMINDER relation which origin questionOption is activated
        for (QuestionRelation questionRelation : reminderQuestion.getQuestionRelations()) {
            if (!questionRelation.isAReminder()) {
                continue;
            }

            //Find QuestionOption for this relation
            QuestionOption questionOption = findQuestionOption(questionRelation);
            if (questionOption == null) {
                continue;
            }

            //Annotate questionOption to check
            this.reminderTriggers.add(questionOption);
        }
    }

    /**
     * Checks if the given questionOption is activated
     */
    private boolean isSelected(QuestionOption questionOption) {
        Option currentOption = ReadWriteDB.readOptionAnswered(questionOption.getQuestion());
        return currentOption != null
                && currentOption.getId_option() == questionOption.getOption().getId_option();
    }


}
