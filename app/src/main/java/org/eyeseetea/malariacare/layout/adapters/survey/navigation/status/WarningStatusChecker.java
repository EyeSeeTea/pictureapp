package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;

/**
 * Checker that helps to decide if a node is visible or not according to the current survey values.
 * This policy will be attached to those nodes with a Reminder relationship
 * Created by arrizabalaga on 13/07/16.
 */
public class WarningStatusChecker extends StatusChecker {

    QuestionThresholdDB mQuestionThresholdDB;
    QuestionOptionDB mQuestionOptionDB;

    public WarningStatusChecker(QuestionDB warningQuestionDB) {
        initWarningTrigger(warningQuestionDB);
    }

    @Override
    public boolean isEnabled() {
        //Warning not built yet -> false
        if (mQuestionThresholdDB == null || mQuestionOptionDB == null) {
            return false;
        }

        //Get current values in DB
        QuestionDB questionDBWithOption = mQuestionOptionDB.getQuestionDB();
        ValueDB optionValueDB = questionDBWithOption.getValueBySession();
        ValueDB intValueDB = mQuestionThresholdDB.getQuestionDB().getValueBySession();

        //A question is not answered yet -> false
        if (optionValueDB == null || optionValueDB.getOptionDB() == null || intValueDB == null) {
            return false;
        }
        //The option for this warning has not been selected
        if (optionValueDB.getId_option() != mQuestionOptionDB.getOptionDB().getId_option()) {
            return false;
        }
        //If current int value NOT in threshold -> the warning is activated
        return !mQuestionThresholdDB.isInThreshold(intValueDB.getValue());
    }

    @Override
    public boolean isVisibleInReview() {
        return false;
    }

    private void initWarningTrigger(QuestionDB reminderQuestionDB) {

        //Look for a WARNING relation which origin mQuestionOptionDB + mQuestionThresholdDB activates this
        for (QuestionRelationDB questionRelationDB : reminderQuestionDB.getQuestionRelationDBs()) {
            if (!questionRelationDB.isAWarning()) {
                continue;
            }

            //Find QuestionOptionDB for this relation
            QuestionOptionDB questionOptionDB = findQuestionOption(questionRelationDB);
            if (questionOptionDB == null) {
                continue;
            }

            //Annotate mQuestionOptionDB and threshold to check
            this.mQuestionOptionDB = questionOptionDB;
            this.mQuestionThresholdDB = questionOptionDB.getQuestionThreshold();
            return;
        }
    }

    public QuestionDB getQuestionToSubscribeFromThreshold() {
        if (mQuestionThresholdDB == null) {
            return null;
        }

        return mQuestionThresholdDB.getQuestionDB();
    }

    public QuestionDB getQuestionToSubscribeFromOption() {
        if (mQuestionOptionDB == null) {
            return null;
        }

        return mQuestionOptionDB.getQuestionDB();
    }

}
