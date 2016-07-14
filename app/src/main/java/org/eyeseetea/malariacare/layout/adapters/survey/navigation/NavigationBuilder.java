package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Tab;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arrizabalaga on 2/06/16.
 */
public class NavigationBuilder {

    private static String TAG="NavigationBuilder";

    private static NavigationBuilder instance;

    private Map<Long,QuestionWarning> warningMap;

    private NavigationBuilder(){
    }

    public static NavigationBuilder getInstance(){
        if(instance==null){
            instance=new NavigationBuilder();
        }
        return instance;
    }

    /**
     * Returns a navigation controller so you can navigate through questions according to answers
     * @param tab
     * @return
     */
    public NavigationController buildController(Tab tab){
        //No tab -> nothing to build
        if(tab==null){
            return null;
        }

        Log.d(TAG,String.format("build(%s)",tab.getName()));
        this.warningMap = new HashMap<>();
        Question rootQuestion = Question.findRootQuestion(tab);

        //NO first question -> nothing to build
        if(rootQuestion==null){
            return null;
        }
        QuestionNode rootNode = buildNode(rootQuestion);
        return new NavigationController(rootNode);
    }

    /**
     * Builds navigation options from the given question
     * @param currentQuestion
     * @return
     */
    private QuestionNode buildNode(Question currentQuestion){
        //No question -> no node
        if(currentQuestion==null){
            return null;
        }
        QuestionNode currentNode = new QuestionNode(currentQuestion);
        //Add children navigation
        buildChildren(currentNode);
        //Add sibling navigation
        buildSibling(currentNode);
        //Add counters
        buildCounters(currentNode);
//        //Add warnings
//        buildWarnings(currentNode);

        return currentNode;
    }

    /**
     * Adds navigation options according to answers (children questions)
     * @param currentNode
     */
    private void buildChildren(QuestionNode currentNode){

        //precondition: options and some relations
        if(!withOptionsAndRelations(currentNode)){
            return;
        }

        Question currentQuestion=currentNode.getQuestion();
        Answer currentAnswer=currentQuestion.getAnswer();
        for(Option option:currentAnswer.getOptions()){
            Question firstChildrenQuestion = currentQuestion.findFirstChildrenByOption(option);

            //No child question for this option -> next
            if(firstChildrenQuestion==null){
                continue;
            }

            Log.d(TAG,String.format("'%s' + '%s' --> '%s'",currentQuestion.getCode(),option.getName(),firstChildrenQuestion.getCode()));
            //Build navigation from there
            QuestionNode childNode;
            //Option that references self
            if(currentNode.getQuestion().getId_question()==firstChildrenQuestion.getId_question()){
                childNode=currentNode;
            }else{
                //Any other child question
                childNode=buildNode(firstChildrenQuestion);
            }

            //Add navigation by option to current node
            currentNode.addNavigation(option,childNode);
        }
    }

    /**
     * Adds navigation no matter what answer is given (sibling question)
     * @param currentNode
     */
    private void buildSibling(QuestionNode currentNode){
        Question nextQuestion = currentNode.getQuestion().getSibling();
        //No next question
        if(nextQuestion==null){
            return;
        }
        QuestionNode nextNode = buildNode(nextQuestion);
        currentNode.setSibling(nextNode);
    }

    /**
     * Adds counters to this node
     * @param currentNode
     */
    private void buildCounters(QuestionNode currentNode){
        //precondition: options and some relations
        if(!withOptionsAndRelations(currentNode)){
            return;
        }

        Question currentQuestion=currentNode.getQuestion();
        Answer currentAnswer=currentQuestion.getAnswer();
        for(Option option:currentAnswer.getOptions()){
            Question optionCounter = currentQuestion.findCounterByOption(option);
            //no counter -> try next option
            if(optionCounter==null){
                continue;
            }
            //found a counter -> annotate it
            currentNode.addCounter(option,optionCounter);
        }
    }

    /**
     * Adds warnings related to this node to the graph
     * @param questionNode
     */
    private void buildWarnings(QuestionNode questionNode){
        Question currentQuestion=questionNode.getQuestion();
        if(currentQuestion==null){
            return;
        }

        if(withOptionsAndRelations(questionNode)){
            buildWarningsFromOptions(questionNode);
        }else{
            buildWarningsFromValue(questionNode);
        }
    }

    /**
     * Adds/Completes a warning with the given questionNode (question with value)
     * @param questionNode
     */
    private void buildWarningsFromOptions(QuestionNode questionNode) {
        //Each option might have a warning associated
        for(QuestionOption questionOption:questionNode.getQuestion().getQuestionOption()){
            Match match =questionOption.getMatch();
            if(match==null){continue;}

            Question warningQuestion=match.getQuestionFromRelationWithType(QuestionRelation.WARNING);
            if(warningQuestion==null){continue;}
            QuestionWarning questionWarning = this.warningMap.get(warningQuestion.getId_question());

            //Already built (this question is second in order)
            if(questionWarning!=null){
                questionWarning.triggersWithOption(questionNode);
            }else{
                //Question with option comes first with in graph
                questionWarning = QuestionWarning.buildParentWithOption(questionNode,warningQuestion);
                warningMap.put(warningQuestion.getId_question(),questionWarning);
            }
        }
    }

    /**
     * Adds/Completes a warning with the given questionNode (question with options)
     * @param questionNode
     */
    private void buildWarningsFromValue(QuestionNode questionNode) {

        //Each threshold will have a warning associated
        for(QuestionThreshold questionThreshold:questionNode.getQuestion().getQuestionThresholds()){
            Match match = questionThreshold.getMatch();
            if(match==null){continue;}

            Question warningQuestion=match.getQuestionFromRelationWithType(QuestionRelation.WARNING);
            QuestionWarning questionWarning = this.warningMap.get(warningQuestion.getId_question());

            //Already built (this question is second in order)
            if(questionWarning!=null){
                questionWarning.triggersWithValue(questionNode);
            }else{
                //Question with value comes first with in graph
                questionWarning = QuestionWarning.buildParentWithValue(questionNode,warningQuestion);
                this.warningMap.put(warningQuestion.getId_question(),questionWarning);
            }
        }
    }

    /**
     * Checks if the currentNode requires knitting children, counters, ...
     * @param currentNode
     * @return
     */
    private boolean withOptionsAndRelations(QuestionNode currentNode){
        Question currentQuestion=currentNode.getQuestion();
        //No children questions -> no children ||counters to build
        if(currentQuestion==null || !currentQuestion.hasOutputWithOptions() || currentQuestion.getQuestionOption().size()==0){
            return false;
        }

        //there might be something related to build
        return true;
    }

}
