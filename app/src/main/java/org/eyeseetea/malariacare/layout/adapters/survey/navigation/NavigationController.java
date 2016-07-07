package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller in charge of moving next, previous according to the answers and state of questions.
 * Created by arrizabalaga on 2/06/16.
 */
public class NavigationController {
    private static final int FIRST_MOVE_IDX = -1;
    private static final String TAG="NavigationController";

    /**
     * Points to the first question
     */
    private QuestionNode rootNode;
    /**
     * List of visited questions
     */
    private List<QuestionNode> visited;

    /**
     * Current position in the list of questions
     */
    private int currentPosition;

    /**
     * Current total of questions in the list of questions
     */
    private int currentTotal;

    /**
     * Flag used to block the swip to the forward question if a question option is clicked
     */
    public static boolean isMovingToForward=false;


    public NavigationController(QuestionNode rootNode){
        this.rootNode = rootNode;
        this.visited=new ArrayList<>();
        this.currentPosition=-1;
    }

    public boolean isFirstQuestion(){
        return this.currentPosition==0;
    }

    public void setTotalPages(int total){
        currentTotal=total;
    }

    public int getTotalPages(){
        return currentTotal;
    }

    public int getCurrentPage(){
        return this.currentPosition;
    }

    public Question getCurrentQuestion(){
        QuestionNode currentNode=getCurrentNode();
        if(currentNode==null){
            Log.w(TAG,"getCurrentQuestion()->Nothing here (have you made the first 'next'?");
            return null;
        }
        return currentNode.getQuestion();
    }

    /**
     * Tells if you can move forward:
     *  - Not even started: true
     *  -
     * Cann
     * @return
     */
    public boolean isNextAllowed(){
        if(isMovingToForward)
            return false;
        QuestionNode currentQuestionNode=getCurrentNode();
        //not even start
        if(currentQuestionNode==null){
            return true;
        }

        //Get value for current
        Question currentQuestion=getCurrentQuestion();
        Value currentValue = currentQuestion.getValueBySession();

        //Cannot move without answer
        if(currentValue==null){
            Log.d(TAG,"isNextAllowed()->You must answer first");
            return false;
        }

        Option currentOption = currentValue==null?null:currentValue.getOption();
        //Find next node with current option
        boolean isAllowed=findNext(currentOption)!=null;
        Log.d(TAG,String.format("isNextAllowed()->%b",isAllowed));
        return isAllowed;
    }

    /**
     * Asks for next question no matter what answer has been given (just sibling)
     * @return
     */
    public Question next(){
        return this.next(null);
    }

    public Question next(Option option){
        Log.d(TAG,String.format("next(%s)...",option==null?"":option.getName()));
        //Find next node
        QuestionNode nextNode=findNext(option);

        //No next
        if(nextNode==null){
            return null;
        }

        //Trigger counters
        if(!isInitialMove()) {
            getCurrentNode().increaseRepetitions(option);
        }

        //Found
        visit(nextNode);
        Question nextQuestion=nextNode.getQuestion();

        //Return next question
        Log.d(TAG,String.format("next(%s)->%s",option==null?"":option.getName(),nextQuestion.getCode()));
        return nextNode.getQuestion();
    }

    /**
     * Returns the previous question
     * @return
     */
    public Question previous(){
        Log.d(TAG,"previous()...");
        //First position -> cannot move
        if(this.currentPosition<=0){
            Log.d(TAG,String.format("previous()->No previous question"));
            return null;
        }

        //Moving backwars removes current node in screen
        this.visited.remove(currentPosition);
        currentPosition--;

        //Return the 'new' last question
        Question previousQuestion=getCurrentNode().getQuestion();
        Log.d(TAG,String.format("previous()->%s",previousQuestion.getCode()));
        return previousQuestion;
    }

    /**
     * Returns the current node in screen
     * @return
     */
    private QuestionNode getCurrentNode(){
        if(currentPosition<0 || currentPosition>=this.visited.size()){
            Log.w(TAG,String.format("getCurrentNode(%d)->Nothing there",currentPosition));
            return null;
        }
        return this.visited.get(currentPosition);
    }

    /**
     * Tells if this is the first move (first question will be shown)
     * @return
     */
    private boolean isInitialMove(){
        return this.currentPosition== FIRST_MOVE_IDX;
    }

    /**
     * Annotates next question in screen
     * @param nextNode
     */
    private void visit(QuestionNode nextNode){
        //Self references are neither moving the counter nor adding a new position
        if(isALoop(nextNode)){
            Log.d(TAG,String.format("visit(%s) -> Not advancing",nextNode.getQuestion().getCode()));
            return;
        }

        //Moving from a warning replaces warning itself instead of moving forward
        if(getCurrentNode() instanceof QuestionWarning){
            visited.remove(currentPosition);
            currentPosition--;
        }

        //annotate new current node and advance counter
        currentPosition++;
        Log.d(TAG,String.format("visit(%s) -> In position %d",nextNode.getQuestion().getCode(),currentPosition));
        visited.add(nextNode);
    }

    /**
     * Goes back to warning that will be showed right before the first question in the validation
     * @param questionWarning
     */
    private void visit(QuestionWarning questionWarning){
        //Find position of 'parentQuestion' in visited
        QuestionNode firstQuestionInWarning = questionWarning.next(null);

        int i;
        for(i=currentPosition;i>=0;i--){
            //Get current Question in reverse traverse
            QuestionNode iQuestionNode=this.visited.get(i);
            //Remove value and visit
            ReadWriteDB.deleteValue(iQuestionNode.getQuestion());
            visited.remove(i);
            //Found parent position
            if(iQuestionNode.getQuestion().getId_question()==firstQuestionInWarning.getQuestion().getId_question()){
                currentPosition=i;
                break;
            }
        }

        //The warning is added right where the first of its questions was
        visited.add(questionWarning);
    }

    public boolean hasNext(Option option){
        return findNext(option)!=null;
    }

    public boolean hasPrevious(){
        return (currentPosition-1)>=0;
    }

    /**
     * Moves state back to first question
     */
    public void first(){
        currentPosition=-1;
        visited.clear();
        next(null);
    }

    private QuestionNode findNext(Option option){
        Log.d(TAG,String.format("findNext(%s)...",option==null?"":option.getCode()));

        //First movement (entering survey)
        if(isInitialMove()){
            Log.d(TAG,String.format("findNext(%s)-> Initial movement",option==null?"":option.getCode()));
            return this.rootNode;
        }

        QuestionNode nextNode=getCurrentNode().next(option);

        //Survey finished -> No more questions
        if(nextNode==null){
            Log.d(TAG,String.format("findNext(%s)-> Survey finished",option==null?"":option.getCode()));
            return null;
        }

        //Return next question
        Log.d(TAG,String.format("findNext(%s)->%s",option==null?"":option.getCode(),nextNode.getQuestion().getCode()));
        return nextNode;
    }

    /**
     * Checks if the given nextNode is a loop with the current one
     * @param nextNode
     * @return
     */
    private boolean isALoop(QuestionNode nextNode){
        if(nextNode==null || getCurrentNode()==null){
            return false;
        }
        return nextNode.getQuestion().getId_question()==this.getCurrentNode().getQuestion().getId_question();
    }
}
