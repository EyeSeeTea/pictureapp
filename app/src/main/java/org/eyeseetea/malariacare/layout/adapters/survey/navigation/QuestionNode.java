package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.ReminderStatusChecker;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.StatusChecker;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.WarningStatusChecker;
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
     * Parent node
     */
    QuestionNode parentNode;
    /**
     * QuestionDB here
     */
    private QuestionDB mQuestionDB;
    /**
     * Where to go given an option (children questions)
     */
    private Map<Long, QuestionNode> navigation;
    /**
     * QuestionDB counters associated to each option of the mQuestionDB
     */
    private Map<Long, QuestionCounter> counters;
    /**
     * List of warning that can be triggered once this mQuestionDB is answer
     */
    private List<QuestionNode> warnings;
    /**
     * StatusChecker (provides info related to node state according to values)
     */
    private StatusChecker statusChecker;
    /**
     * Next mQuestionDB (sibling)
     */
    private QuestionNode sibling;

    /**
     * Previous mQuestionDB (sibling). Required to rewind to warnings
     */
    private QuestionNode previousSibling;

    public QuestionNode(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
        this.navigation = new HashMap<>();
        this.counters = new HashMap<>();
        this.statusChecker = buildStatusChecker();
        this.warnings = new ArrayList<>();
    }

    public QuestionDB getQuestionDB() {
        return this.mQuestionDB;
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
    }

    /**
     * From this mQuestionDB given the optionDB you will move to
     */
    public void addNavigation(OptionDB optionDB, QuestionNode nextNode) {
        //something wrong -> nothing to add
        if (optionDB == null || nextNode == null) {
            return;
        }

        //Add parentNode to children
        nextNode.setParentNode(this);
        //Annotate navigation
        this.navigation.put(optionDB.getId_option(), nextNode);
    }

    /**
     * The given optionDB triggers a counter to save the number of times its been answered
     */
    public void addCounter(OptionDB optionDB, QuestionDB questionDB) {
        //something wrong -> nothing to add
        if (optionDB == null || questionDB == null) {
            return;
        }

        //Build a counter
        QuestionCounter questionCounter = new QuestionCounter(questionDB);

        //Add counter to optionDB
        this.counters.put(optionDB.getId_option(), questionCounter);
    }

    /**
     * Adds a warning to this node.
     * This means that every time you about to leave this node every related warning node
     * will be checked in order to see if its triggered or not according to the current values.
     */
    public void addWarning(QuestionNode warningNode) {
        //Only nodes with type warning allows here
        if (warningNode == null || warningNode.getQuestionDB().getOutput() != Constants.WARNING) {
            return;
        }

        this.warnings.add(warningNode);
    }

    /**
     * Looks for a related warning that is activated
     */
    public QuestionNode findWarningActivated() {
        for (QuestionNode questionNode : this.warnings) {
            if (questionNode.isEnabled()) {
                return questionNode;
            }
        }
        return null;
    }

    /**
     * Sets the parent of this questionnode
     */
    public void setParentNode(QuestionNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * gets the next sibling (same parent mQuestionDB)
     */
    public QuestionNode getSibling() {
        return this.sibling;
    }

    /**
     * Sets the next sibling (same parent mQuestionDB)
     */
    public void setSibling(QuestionNode sibling) {
        this.sibling = sibling;
        //Siblings share same parent
        this.sibling.parentNode = this.parentNode;
    }

    public QuestionNode getPreviousSibling() {
        return this.previousSibling;
    }

    /**
     * Sets the previous sibling
     */
    public void setPreviousSibling(QuestionNode previousSibling) {
        this.previousSibling = previousSibling;
    }

    /**
     * Returns previous actived node in navation
     */
    public QuestionNode previous() {
        //No previousSibling -> return parent
        if (this.previousSibling == null) {
            return this.parentNode;
        }
        //Previous not enable -> move backwards recursively
        if (!this.previousSibling.isEnabled()) {
            return this.previousSibling.previous();
        }
        //Returns previous node
        return this.previousSibling;
    }

    /**
     * Returns previous activated node in navigation
     */
    public QuestionNode next() {
        //Next not enable -> move forward recursively
        if (!this.sibling.isEnabled()) {
            return this.sibling.next(null);
        }
        //Go to next node
        return this.sibling;
    }


    /**
     * Returns next mQuestionDB given an optionDB
     */
    public QuestionNode next(OptionDB optionDB) {
        //Find next (optionDB, sibling, parent)
        QuestionNode nextNode = nextAnyWay(optionDB);
        if (nextNode == null) {
            return null;
        }
        //If nextNode is off -> move forward (recursively)
        while (nextNode != null && !nextNode.isEnabled()) {
            nextNode = nextNode.nextAnyWay(null);
        }
        return nextNode;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled() {
        return this.statusChecker != null && this.statusChecker.isEnabled();
    }

    public boolean isVisibleInReview() {
        return this.statusChecker != null && this.statusChecker.isVisibleInReview();
    }

    private QuestionNode nextAnyWay(OptionDB optionDB) {
        //Try children
        QuestionNode nextNode = nextByOption(optionDB);
        if (nextNode != null) {
            return nextNode;
        }

        //Try sibling same level
        nextNode = nextBySibling();
        if (nextNode != null) {
            return nextNode;
        }

        //No parent -> no where to go
        if (parentNode == null) {
            return null;
        }

        //Parent -> Try parent's sibling
        return this.parentNode.nextAnyWay(null);
    }

    /**
     * Returns the status checker policy
     */
    public StatusChecker getStatusChecker() {
        return statusChecker;
    }

    /**
     * Builds a statusChecker according to the type of mQuestionDB
     */
    private StatusChecker buildStatusChecker() {
        if (this.mQuestionDB == null) {
            return null;
        }

        switch (this.getQuestionDB().getOutput()) {
            case Constants.WARNING:
                return new WarningStatusChecker(this.mQuestionDB);
            case Constants.REMINDER:
                return new ReminderStatusChecker(this.mQuestionDB);
            default:
                return new StatusChecker();
        }
    }

    /**
     * Returns navigation from here with the given optionDB
     */
    private QuestionNode nextByOption(OptionDB optionDB) {
        if (optionDB == null) {
            return null;
        }

        return this.navigation.get(optionDB.getId_option());
    }

    /**
     * Returns next mQuestionDB no matter what (moves to sibling)
     */
    private QuestionNode nextBySibling() {
        //NO sibling ->nowhere to go
        if (this.sibling == null) {
            return null;
        }
        return this.sibling;
    }

    /**
     * Updates the mQuestionDB counter for the given optionDB
     *
     * @return true when a counter has been incremented
     */
    public boolean increaseRepetitions(OptionDB optionDB) {
        if (optionDB == null) {
            return false;
        }

        QuestionCounter questionCounter = this.counters.get(optionDB.getId_option());
        if (questionCounter == null) {
            return false;
        }

        //something to inc -> true
        questionCounter.increaseRepetitions();
        return true;
    }

    public Map<Long, QuestionCounter> getCountersMap() {
        return counters;
    }
}
