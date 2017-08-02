package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller in charge of moving next, previous according to the answers and state of questions.
 * Created by arrizabalaga on 2/06/16.
 */
public class NavigationController {
    //TODO: Refactor to clean architecture

    private static final int FIRST_MOVE_IDX = -1;
    private static final String TAG = "NavigationController";
    /**
     * Flag used to block the swip to the forward question if a question option is clicked
     */
    public static boolean isMovingToForward = false;
    /**
     * Points to the first question
     */
    private QuestionNode rootNode;
    /**
     * List of visited questions
     */
    private List<QuestionNode> visited;
    /**
     * A reference to a node that is shown it wont be shown up in previous movement
     * such as Warnings
     */
    private QuestionNode nonVisibleNode;
    /**
     * Current position in the list of questions
     */
    private int currentPosition;
    /**
     * Current total of questions in the list of questions
     */
    private int currentTotal;


    public NavigationController(QuestionNode rootNode) {
        this.rootNode = rootNode;
        this.visited = new ArrayList<>();
        this.currentPosition = -1;
    }

    public int getCurrentTotalPages() {
        return currentTotal;
    }

    public void setTotalPages(int total) {
        currentTotal = total;
    }

    public int getCurrentPage() {
        return this.currentPosition;
    }

    public Question getCurrentQuestion() {
        QuestionNode currentNode = getCurrentNode();
        if (currentNode == null) {
            Log.w(TAG, "getCurrentQuestion()->Nothing here (have you made the first 'next'?");
            return null;
        }
        return currentNode.getQuestion();
    }

    public Tab getCurrentTab() {
        if (getCurrentQuestion() == null) return null;
        return getCurrentQuestion().getHeaderDB().getTab();
    }

    /**
     * Tells if you can move forward:
     * - Not even started: true
     * -
     * Cann
     */
    public boolean isNextAllowed() {
        if (isMovingToForward) {
            return false;
        }
        QuestionNode currentQuestionNode = getCurrentNode();
        //not even start
        if (currentQuestionNode == null) {
            return true;
        }

        //Get value for current
        Question currentQuestion = getCurrentQuestion();
        Value currentValue = currentQuestion.getValueBySession();

        //Cannot move without answer
        if (currentValue == null) {
            Log.d(TAG, "isNextAllowed()->You must answer first");
            return false;
        }

        OptionDB currentOptionDB = currentValue == null ? null : currentValue.getOptionDB();
        //Find next node with current option
        boolean isAllowed = findNext(currentOptionDB) != null;
        Log.d(TAG, String.format("isNextAllowed()->%b", isAllowed));
        return isAllowed;
    }

    /**
     * Asks for next question no matter what answer has been given (just sibling)
     */
    public Question next() {
        return this.next(null);
    }

    public void increaseCounterRepetitions(OptionDB optionDB) {
        getCurrentNode().increaseRepetitions(optionDB);
    }

    public Question next(OptionDB optionDB) {
        Log.d(TAG, String.format("next(%s)...", optionDB == null ? "" : optionDB.getCode()));
        QuestionNode nextNode;

        //Trigger counters -> no movement
        if (!isInitialMove() && existsPendingCounter(optionDB)) {
            Log.d(TAG, String.format("next(%s)->%s", optionDB == null ? "" : optionDB.getCode(),
                    getCurrentQuestion().getCode()));
            return getCurrentQuestion();
        }

        //First movement -> nothing to check
        if (isInitialMove()) {
            nextNode = findNext(optionDB);
        } else {
            //Check if current values trigger a warning
            nextNode = getCurrentNode().findWarningActivated();
        }

        //No warning activated -> try normal
        if (nextNode == null) {
            //No trigger -> next as usual
            nextNode = findNext(optionDB);
        }

        //No next
        if (nextNode == null) {
            return null;
        }

        //Found
        visit(nextNode);
        Question nextQuestion = nextNode.getQuestion();


        //Return next question
        Log.d(TAG, String.format("next(%s)->%s", optionDB == null ? "" : optionDB.getCode(),
                nextQuestion.getCode()));
        return nextNode.getQuestion();
    }

    public boolean existsPendingCounter(OptionDB optionDB) {
        Map<Long, QuestionCounter> counters = getCurrentNode().getCountersMap();

        if (counters == null || counters.size() == 0) {
            return false;
        }

        QuestionCounter questionCounter = counters.get(optionDB.getId_option());
        Integer limit = (int) Math.floor(optionDB.getFactor());

        if (questionCounter == null) return false;
        return (questionCounter.isMaxCounterLimit(limit)) ? false : true;
    }

    /**
     * Returns the previous question
     */
    public Question previous() {

        Log.d(TAG, "previous()...");
        //First position -> cannot move
        if (this.currentPosition <= 0) {
            Log.d(TAG, String.format("previous()->No previous question"));
            return null;
        }

        //Moving backwards removes current node in screen (unless a special node)
        if (nonVisibleNode == null) {
            this.visited.remove(currentPosition);
            currentPosition--;
        } else {
            //Abandoning temporal node
            nonVisibleNode = null;
        }

        //Return the 'new' last question
        Question previousQuestion = getCurrentNode().getQuestion();
        Log.d(TAG, String.format("previous()->%s", previousQuestion.getCode()));
        return previousQuestion;
    }

    /**
     * Returns the current node in screen
     */
    private QuestionNode getCurrentNode() {

        //Temporal nonVisibleNode comes first
        if (nonVisibleNode != null) {
            return nonVisibleNode;
        }

        //Most of times simply returns the las visited node
        if (currentPosition < 0 || currentPosition >= this.visited.size()) {
            Log.w(TAG, String.format("getCurrentNode(%d)->Nothing there", currentPosition));
            return null;
        }
        return this.visited.get(currentPosition);
    }

    /**
     * Tells if this is the first move (first question will be shown)
     */
    private boolean isInitialMove() {
        return this.currentPosition == FIRST_MOVE_IDX;
    }

    /**
     * Annotates next question in screen
     */
    private void visit(QuestionNode nextNode) {
        //Self references are neither moving the counter nor adding a new position
        if (isALoop(nextNode)) {
            Log.d(TAG,
                    String.format("visit(%s) -> Not advancing", nextNode.getQuestion().getCode()));
            return;
        }

        //This node wont be shown in previous move (warnings)
        if (!nextNode.isVisibleInReview()) {
            //If the survey is sent we need hide the reminder question.
            if (Session.getMalariaSurvey() != null && !Session.getMalariaSurvey().isInProgress()) {
                for (QuestionRelation questionRelation : nextNode.getQuestion()
                        .getQuestionRelations()) {
                    if (questionRelation.isAReminder()) {
                        //is a reminder -> next
                        nonVisibleNode = nextNode;

                        if (nextNode == null) {
                            return;
                        }
                        next(null);
                        return;
                    }
                }
            }
            Log.d(TAG,
                    String.format("visit(%s) -> In position %d", nextNode.getQuestion().getCode(),
                            currentPosition));
            //Requires a rewind leaving its parent as last visited
            rewindVisited(nextNode);
            //Annotate 'floating' node
            nonVisibleNode = nextNode;
            return;
        }

        //annotate new current node and advance counter
        nonVisibleNode = null;
        currentPosition++;
        Log.d(TAG, String.format("visit(%s) -> In position %d", nextNode.getQuestion().getCode(),
                currentPosition));
        visited.add(nextNode);
    }

    public boolean hasNext(OptionDB optionDB) {
        return findNext(optionDB) != null;
    }

    public boolean hasPrevious() {
        return (currentPosition - 1) >= 0;
    }

    /**
     * Moves state back to first question
     */
    public void first() {
        currentPosition = -1;
        visited.clear();
        next(null);
    }

    private QuestionNode findNext(OptionDB optionDB) {
        Log.d(TAG, String.format("findNext(%s)...",
                optionDB == null ? "" : optionDB.getInternationalizedName()));

        //First movement (entering survey)
        if (isInitialMove()) {
            Log.d(TAG, String.format("findNext(%s)-> Initial movement",
                    optionDB == null ? "" : optionDB.getInternationalizedName()));
            return this.rootNode;
        }
        Question actualQuestion = getCurrentNode().getQuestion();
        QuestionNode nextNode;
        nextNode = getCurrentNode().next(optionDB);
        if (nextNode != null && (
                actualQuestion.getHeaderDB().getTab().getType() == Constants.TAB_MULTI_QUESTION
                        || actualQuestion.getHeaderDB().getTab().getType()
                        == Constants.TAB_DYNAMIC_TREATMENT
                        || nextNode.getQuestion().getOutput() == Constants.HIDDEN)) {
            while (nextNode != null && (nextNode.getQuestion().getHeaderDB().getTab().equals(
                    actualQuestion.getHeaderDB().getTab())
                    || nextNode.getQuestion().getOutput() == Constants.HIDDEN)) {
                OptionDB optionDBNext = nextNode.getQuestion().getOptionBySession();
                nextNode = nextNode.next(optionDBNext);
            }
        }

        //Survey finished -> No more questions
        if (nextNode == null) {
            Map<Long, QuestionCounter> counters = getCurrentNode().getCountersMap();
            if (counters == null || counters.size() == 0) {
                Log.d(TAG, String.format("findNext(%s)-> Survey finished",
                        optionDB == null ? "" : optionDB.getInternationalizedName()));
                return null;
            }
            if (optionDB != null && counters.containsKey(optionDB.getId_option())) {
                QuestionCounter questionCounter = counters.get(optionDB.getId_option());
                Integer limit = (int) Math.floor(optionDB.getFactor());
                Log.d(TAG, String.format("findNext(%s)-> Survey(%s)finished",
                        optionDB == null ? "" : optionDB.getInternationalizedName(),
                        (questionCounter.isMaxCounterLimit(limit)) ? " " : " not "));
                return (questionCounter.isMaxCounterLimit(limit)) ? null
                        : getCurrentNode().getPreviousSibling();
            }
        }

        //Return next question
        if (nextNode != null && nextNode.getQuestion() != null) {
            Log.d(TAG, String.format("findNext(%s)->%s",
                    optionDB == null ? "" : optionDB.getInternationalizedName(),
                    nextNode.getQuestion().getCode() + ""));
        }
        return nextNode;
    }

    /**
     * Checks if the given nextNode is a loop with the current one
     */
    private boolean isALoop(QuestionNode nextNode) {
        if (nextNode == null || getCurrentNode() == null) {
            return false;
        }
        return nextNode.getQuestion().getId_question()
                == this.getCurrentNode().getQuestion().getId_question();
    }

    /**
     * Rewinds visited list until you find the parent of the warning (or none)
     */
    private void rewindVisited(QuestionNode warningNode) {
        if (warningNode == null) {
            return;
        }

        while (getCurrentNode() != null && getCurrentNode() != warningNode.previous()) {
            getCurrentQuestion().deleteValueBySession();
            previous();
        }
    }
}
