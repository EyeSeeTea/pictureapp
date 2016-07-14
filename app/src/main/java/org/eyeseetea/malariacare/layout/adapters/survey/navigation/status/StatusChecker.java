package org.eyeseetea.malariacare.layout.adapters.survey.navigation.status;

/**
 * Base class that helps a QuestionNode to decide whether isEnabled (visitable) or
 * can be seen during a previous/review action.
 * Created by arrizabalaga on 13/07/16.
 */
public class StatusChecker {
    /**
     * By default every node is enabled
     * @return
     */
    public boolean isEnabled(){
        return true;
    }

    /**
     * By default every node is visible during review
     * @return
     */
    public boolean isVisibleInReview(){
        return true;
    }
}
