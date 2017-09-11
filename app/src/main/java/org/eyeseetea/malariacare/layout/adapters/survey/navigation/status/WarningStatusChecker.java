package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.data.database.model.Value;

/**
 * Checker that helps to decide if a node is visible or not according to the current survey values.
 * This policy will be attached to those nodes with a Reminder relationship
 * Created by arrizabalaga on 13/07/16.
 */
public class WarningStatusChecker extends StatusChecker {

    QuestionThreshold questionThreshold;
    QuestionOption questionOption;

    public WarningStatusChecker(Question warningQuestion) {
        initWarningTrigger(warningQuestion);
    }

    @Override
    public boolean isEnabled() {
        //Warning not built yet -> false
        if (questionThreshold == null || questionOption == null) {
            return false;
        }

        //Get current values in DB
        Question questionWithOption = questionOption.getQuestion();
        Value optionValue = questionWithOption.getValueBySession();
        Value intValue = questionThreshold.getQuestion().getValueBySession();

        //A question is not answered yet -> false
        if (optionValue == null || optionValue.getOption() == null || intValue == null) {
            return false;
        }
        //The option for this warning has not been selected
        if (optionValue.getId_option() != questionOption.getOption().getId_option()) {
            return false;
        }
        //If current int value NOT in threshold -> the warning is activated
        return !questionThreshold.isInThreshold(intValue.getValue());
    }

    @Override
    public boolean isVisibleInReview() {
        return false;
    }

    private void initWarningTrigger(Question reminderQuestion) {

        //Look for a WARNING relation which origin questionOption + questionThreshold activates this
        for (QuestionRelation questionRelation : reminderQuestion.getQuestionRelations()) {
            if (!questionRelation.isAWarning()) {
                continue;
            }

            //Find QuestionOption for this relation
            QuestionOption questionOption = findQuestionOption(questionRelation);
            if (questionOption == null) {
                continue;
            }

            //Annotate questionOption and threshold to check
            this.questionOption = questionOption;
            this.questionThreshold = questionOption.getQuestionThreshold();
            return;
        }
    }

    public Question getQuestionToSubscribeFromThreshold() {
        if (questionThreshold == null) {
            return null;
        }

        return questionThreshold.getQuestion();
    }

    public Question getQuestionToSubscribeFromOption() {
        if (questionOption == null) {
            return null;
        }

        return questionOption.getQuestion();
    }

}
