package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;

import java.util.HashMap;
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
     * Next question (sibling)
     */
    private QuestionNode sibling;

    public QuestionNode(Question question){
        this.question = question;
        this.navigation = new HashMap<>();
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

        this.navigation.put(option.getId_option(),nextNode);
    }

    /**
     * Sets the next sibling (same parent question)
     * @param sibling
     */
    public void setSibling(QuestionNode sibling){
        this.sibling=sibling;
    }

    /**
     * Returns next question given an option
     * @param option
     * @return
     */
    public QuestionNode next(Option option){
        //No option -> try sibling
        if(option==null){
            return next();
        }

        QuestionNode nextNode=this.navigation.get(option.getId_option());
        //Nowhere to go from here
        if(nextNode==null){
            return null;
        }

        return nextNode;
    }

    /**
     * Returns next question no matter what (moves to sibling)
     * @return
     */
    public QuestionNode next(){
        //NO sibling ->nowhere to go
        if(this.sibling==null){
            return null;
        }

        return this.sibling;
    }


}
