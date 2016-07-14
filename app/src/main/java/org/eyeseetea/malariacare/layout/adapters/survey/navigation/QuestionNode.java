package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.ReminderStatusChecker;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.StatusChecker;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO that represents a node in the graph that tells where to go next
 * Created by arrizabalaga on 2/06/16.
 */
public class QuestionNode {
    /**
     * Question here
     */
    private Question question;
    /**
     * Where to go given an option (children questions)
     */
    private Map<Long,QuestionNode> navigation;

    /**
     * Question counters associated to each option of the question
     */
    private Map<Long,QuestionCounter> counters;

    /**
     * List of warning that can be triggered once this question is answer
     */
    private List<QuestionWarning> warnings;

    /**
     * StatusChecker (provides info related to node state according to values)
     */
    private StatusChecker statusChecker;

    /**
     * Parent node
     */
    QuestionNode parentNode;

    /**
     * Next question (sibling)
     */
    private QuestionNode sibling;

    public QuestionNode(Question question){
        this.question = question;
        this.navigation = new HashMap<>();
        this.counters = new HashMap<>();
        this.warnings = new ArrayList<>();
        this.statusChecker = buildStatusChecker();
    }

    public void setQuestion(Question question){
        this.question=question;
    }

    public Question getQuestion(){
        return this.question;
    }

    /**
     * From this question given the option you will move to
     * @param option
     */
    public void addNavigation(Option option, QuestionNode nextNode){
        //something wrong -> nothing to add
        if(option==null || nextNode==null){
            return;
        }

        //Add parentNode to children
        nextNode.setParentNode(this);
        //Annotate navigation
        this.navigation.put(option.getId_option(),nextNode);
    }

    /**
     * The given option triggers a counter to save the number of times its been answered
     * @param option
     * @param question
     */
    public void addCounter(Option option, Question question){
        //something wrong -> nothing to add
        if(option==null || question==null){
            return;
        }

        //Build a counter
        QuestionCounter questionCounter = new QuestionCounter(question);

        //Add counter to option
        this.counters.put(option.getId_option(),questionCounter);
    }

    /**
     * Adds a warning to check for this node
     * @param questionWarning
     */
    public void addWarning(QuestionWarning questionWarning){
        this.warnings.add(questionWarning);
    }

    /**
     * Sets the parent of this questionnode
     * @param parentNode
     */
    public void setParentNode(QuestionNode parentNode){
        this.parentNode=parentNode;
    }

    /**
     * Sets the next sibling (same parent question)
     * @param sibling
     */
    public void setSibling(QuestionNode sibling){
        this.sibling=sibling;
        //Siblings share same parent
        this.sibling.parentNode=this.parentNode;
    }

    /**
     * Returns next question given an option
     * @param option
     * @return
     */
    public QuestionNode next(Option option){
        //Find next (option, sibling, parent)
        QuestionNode nextNode=nextAnyWay(option);
        if(nextNode==null){
            return null;
        }
        //If nextNode is off -> move forward (recursively)
        while(nextNode!=null && !nextNode.isEnabled()){
            nextNode=nextNode.nextAnyWay(null);
        }
        return nextNode;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled(){
        return this.statusChecker!=null && this.statusChecker.isEnabled();
    }

    public boolean isVisibleInReview(){
        return this.statusChecker!=null && this.statusChecker.isVisibleInReview();
    }

    private QuestionNode nextAnyWay(Option option){
        //Try children
        QuestionNode nextNode = nextByOption(option);
        if(nextNode!=null){
            return nextNode;
        }

        //Try sibling same level
        nextNode = nextBySibling();
        if(nextNode!=null){
            return nextNode;
        }

        //No parent -> no where to go
        if(parentNode==null){
            return null;
        }

        //Parent -> Try parent's sibling
        return this.parentNode.nextBySibling();
    }

    /**
     * Builds a statusChecker according to the type of question
     * @return
     */
    private StatusChecker buildStatusChecker(){
        if(this.question==null){
            return null;
        }

        switch (this.getQuestion().getOutput()){
            case Constants.WARNING:
                //TODO
            case Constants.REMINDER:
                return new ReminderStatusChecker(this.question);
            default:
                return new StatusChecker();
        }
    }

    /**
     * Returns navigation from here with the given option
     * @param option
     * @return
     */
    private QuestionNode nextByOption(Option option){
        if(option==null){
            return null;
        }

        return this.navigation.get(option.getId_option());
    }

    /**
     * Returns next question no matter what (moves to sibling)
     * @return
     */
    private QuestionNode nextBySibling(){
        //NO sibling ->nowhere to go
        if(this.sibling==null){
            return null;
        }

        return this.sibling;
    }

    /**
     * Updates the question counter for the given option
     * @param option
     * @return true when a counter has been incremented
     */
    public boolean increaseRepetitions(Option option){
        if(option==null){
            return false;
        }

        QuestionCounter questionCounter = this.counters.get(option.getId_option());
        if(questionCounter==null){
            return false;
        }

        //something to inc -> true
        questionCounter.increaseRepetitions();
        return true;
    }


}
