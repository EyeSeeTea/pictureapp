package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;

import java.util.HashMap;
import java.util.Map;

/**
 * A special type of questionNode that is only shown as a warning when the values from 2 questions do NOT match as they should.
 * Created by arrizabalaga on 2/06/16.
 */
public class QuestionWarning extends QuestionNode{

    QuestionWarning(Question question){
        super(question);
    }

    /**
     * Reference to the questionNode that holds the options to check for this warning
     */
    private QuestionNode nodeWithOption;

    /**
     * Reference to the questionNode that holds the value to check for this warning
     */
    private QuestionNode nodeWithValue;

    /**
     * Builds a QuestionWarning with que warningQuestion as its inner question and the nodeWithOption as a reference to the
     * first question for this trigger.
     * @param nodeWithOption
     * @param warningQuestion
     * @return
     */
    public static QuestionWarning buildParentWithOption(QuestionNode nodeWithOption, Question warningQuestion){
        QuestionWarning questionWarning = new QuestionWarning(warningQuestion);
        questionWarning.setParentNode(nodeWithOption);
        questionWarning.nodeWithOption=nodeWithOption;
        return questionWarning;
    }

    /**
     * Builds a QuestionWarning with que warningQuestion as its inner question and the nodeWithValue as a reference to the
     * first question for this trigger.
     * @param nodeWithValue
     * @param warningQuestion
     * @return
     */
    public static QuestionWarning buildParentWithValue(QuestionNode nodeWithValue, Question warningQuestion){
        QuestionWarning questionWarning = new QuestionWarning(warningQuestion);
        questionWarning.setParentNode(nodeWithValue);
        questionWarning.nodeWithValue=nodeWithValue;
        return questionWarning;
    }

    /**
     * Annotates the given node as the node (with option) that might trigger this one.
     * @param nodeWithOption
     */
    public void triggersWithOption(QuestionNode nodeWithOption) {
        this.nodeWithOption = nodeWithOption;
        this.nodeWithOption.addWarning(this);
    }

    /**
     * Annotates the given node as the node (with value) that might trigger this one.
     * @param nodeWithValue
     */
    public void triggersWithValue(QuestionNode nodeWithValue) {
        this.nodeWithValue = nodeWithValue;
        this.nodeWithValue.addWarning(this);
    }

    public QuestionNode getNodeWithOption() {
        return nodeWithOption;
    }

    public QuestionNode getNodeWithValue() {
        return nodeWithValue;
    }

    /**
     * Checks if this warning is triggered according to the current values in DB for its questions
     * @return
     */
    public boolean isTriggered(){
        //Warning not built yet -> false
        if(nodeWithOption==null || nodeWithValue==null){
            return false;
        }

        //Get current values in DB
        Question questionWithOption=nodeWithOption.getQuestion();
        Value optionValue = questionWithOption.getValueBySession();
        Value intValue = nodeWithValue.getQuestion().getValueBySession();

        //A question is not answered yet -> false
        if(optionValue==null || optionValue.getOption()==null|| intValue==null){
            return false;
        }

        //Look for activated questionThreshold
        QuestionThreshold questionThreshold = QuestionThreshold.findByQuestionAndOption(questionWithOption,optionValue.getOption());
        if(questionThreshold==null){
            return false;
        }

        //If current int value NOT in threshold -> the warning is activated
        return !questionThreshold.isInThreshold(intValue.getValue());
    }

    /**
     * A QuestionWarning always navigates to the parentNode (first of the two questions involved in the trigger)
     * @param option
     * @return
     */
    @Override
    public QuestionNode next(Option option){
        return this.parentNode;
    }
}
