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
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.WarningStatusChecker;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arrizabalaga on 2/06/16.
 */
public class NavigationBuilder {

    private static String TAG="NavigationBuilder";

    private static NavigationBuilder instance;

    /**
     * Maps that holds the relationships between a Question and the warnings that it might trigger
     */
    private Map<Long,List<QuestionNode>> warningsXQuestion;

    private NavigationBuilder(){
        warningsXQuestion = new HashMap<>();
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

        warningsXQuestion.clear();

        Log.d(TAG,String.format("build(%s)",tab.getName()));
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

        //A warning is added to the map
        annotateWarning(currentNode);
        //A normal node subscribes to its warnings
        subscribeWarnings(currentNode);

        //Add children navigation
        buildChildren(currentNode);
        //Add sibling navigation
        buildSibling(currentNode);
        //Add counters
        buildCounters(currentNode);

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
        Question currentQuestion=currentNode.getQuestion();
        Question nextQuestion = currentQuestion.getSibling();

        //No next question
        if(nextQuestion==null){
            Log.d(TAG,String.format("'%s' -(sibling)-> null",currentQuestion.getCode()));
            return;
        }
        Log.d(TAG,String.format("'%s' -(sibling)-> '%s'",currentQuestion.getCode(),nextQuestion.getCode()));
        QuestionNode nextNode = buildNode(nextQuestion);
        currentNode.setSibling(nextNode);
        nextNode.setPreviousSibling(currentNode);
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

    /**
     * Annotates  the questions involved in this warning
     * @param warningNode
     */
    private void annotateWarning(QuestionNode warningNode){
        //Not a warning -> done
        if(warningNode.getQuestion().getOutput()!=Constants.WARNING){
            return;
        }

        WarningStatusChecker warningStatusChecker =(WarningStatusChecker )warningNode.getStatusChecker();
        Question questionWithThreshold = warningStatusChecker.getQuestionToSubscribeFromThreshold();
        Question questionWithOption = warningStatusChecker.getQuestionToSubscribeFromOption();

        addWarning(questionWithThreshold,warningNode);
        addWarning(questionWithOption,warningNode);
    }

    private void addWarning(Question subscriber, QuestionNode warningNode){
        List<QuestionNode> warnings = this.warningsXQuestion.get(subscriber.getId_question());
        //First warning added
        if(warnings==null){
            warnings = new ArrayList<>();
            this.warningsXQuestion.put(subscriber.getId_question(),warnings);
        }
        //Add new warning to list
        warnings.add(warningNode);
    }

    private void subscribeWarnings(QuestionNode subscriberNode){
        List<QuestionNode> warnings = this.warningsXQuestion.get(subscriberNode.getQuestion().getId_question());
        //No warnings attached
        if(warnings==null){
            return;
        }

        //Subscribe to every warning
        for(QuestionNode warningNode: warnings){
            subscriberNode.addWarning(warningNode);
        }
    }

}
