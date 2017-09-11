package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.fragments.SurveyFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.status.WarningStatusChecker;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationBuilder {
    //TODO: Refactor to clean architecture


    public interface LoadBuildControllerListener {
        void onLoadFinished();
    }

    private LoadBuildControllerListener mLoadBuildControllerListener;

    private static String TAG = "NavigationBuilder";

    private static NavigationBuilder instance;
    private static int MAX_STEPS = 67;
    /**
     * Maps that holds the relationships between a Question and the warnings that it might trigger
     */
    private Map<Long, List<QuestionNode>> warningsXQuestion;
    /**
     * Questions ordered by id
     */
    private int step = 0;

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    private NavigationBuilder() {
        warningsXQuestion = new HashMap<>();
        mMainExecutor = new UIThreadExecutor();
        mAsyncExecutor = new AsyncExecutor();
    }

    public static NavigationBuilder getInstance() {
        if (instance == null) {
            instance = new NavigationBuilder();
        }
        return instance;
    }

    /**
     * Returns a navigation controller so you can navigate through questions according to answers
     */
    public void buildController(Tab tab) throws LoadingNavigationControllerException{

        if (Session.isIsLoadingNavigationController()) {
            Log.d(TAG, "Navigation controller cannot load because it is already loading");
            return;
        }

        //No tab -> nothing to build
        if (tab == null) {
            Log.w(TAG, "Navigation controller cannot load because first tab doesn't exist");
            return;
        }

        warningsXQuestion.clear();

        Log.d(TAG, String.format("build(%s)", tab.getName()));
        final Question rootQuestion = Question.findRootQuestion(tab);

        //NO first question -> nothing to build
        if (rootQuestion == null) {
            Log.w(TAG, "Navigation controller cannot load because root question doesn't exist");
            return;
        }

        mAsyncExecutor.run(new Runnable() {
            @Override
            public void run() {
                buildNavigationController(rootQuestion);
            }
        });

    }

    private void buildNavigationController(Question rootQuestion){
        Log.d(TAG, "Begin loading navigation controller");

        try {
            Session.setIsLoadingNavigationController(true);
            //init steps counter
            step = 0;
            QuestionNode rootNode = buildNode(rootQuestion);

            Session.setNavigationController(new NavigationController(rootNode));

            Session.setIsLoadingNavigationController(false);

            if (mLoadBuildControllerListener != null) {
                notifyLoadFinished();
            }
        } catch (NullPointerException ex) {
            Session.setIsLoadingNavigationController(false);
            new LoadingNavigationControllerException(ex);
        }

        Log.d(TAG, "End loading navigation controller");
    }

    private void notifyLoadFinished() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mLoadBuildControllerListener.onLoadFinished();
            }
        });
    }

    /**
     * Builds navigation options from the given question
     */
    private QuestionNode buildNode(Question currentQuestion) {
        //No question -> no node
        if (currentQuestion == null) {
            return null;
        }

        QuestionNode currentNode = new QuestionNode(currentQuestion);

        //A warning is added to the map
        annotateWarning(currentNode);
        //A normal node subscribes to its warnings
        subscribeWarnings(currentNode);

        nextStepMessage();
        //Add children navigation
        buildChildren(currentNode);
        //Add sibling navigation
        buildSibling(currentNode);
        //Add counters
        buildCounters(currentNode);

        return currentNode;
    }

    private void nextStepMessage() {
        step++;
        int totalSteps = SurveyFragment.progressMessagesCount();
        int messageStep = Math.round(MAX_STEPS / totalSteps);
        if (step % messageStep == 1) {
            SurveyFragment.nextProgressMessage();
        }
    }

    /**
     * Adds navigation options according to answers (children questions)
     */
    private void buildChildren(QuestionNode currentNode) {

        //precondition: options and some relations
        if (!withOptionsAndRelations(currentNode)) {
            return;
        }

        Question currentQuestion = currentNode.getQuestion();
        Answer currentAnswer = currentQuestion.getAnswer();
        for (Option option : currentAnswer.getOptions()) {
            Question firstChildrenQuestion = currentQuestion.findFirstChildrenByOption(option);

            //No child question for this option -> next
            if (firstChildrenQuestion == null) {
                continue;
            }

            Log.d(TAG, String.format("'%s' + '%s' --> '%s'", currentQuestion.getCode(),
                    option.getCode(), firstChildrenQuestion.getCode()));
            //Build navigation from there
            QuestionNode childNode;
            //Option that references self
            if (currentNode.getQuestion().getId_question()
                    == firstChildrenQuestion.getId_question()) {
                childNode = currentNode;
            } else {
                //Any other child question
                childNode = buildNode(firstChildrenQuestion);
            }

            //Add navigation by option to current node
            currentNode.addNavigation(option, childNode);
        }
    }

    /**
     * Adds navigation no matter what answer is given (sibling question)
     */
    private void buildSibling(QuestionNode currentNode) {
        Question currentQuestion = currentNode.getQuestion();
        Question nextQuestion = currentQuestion.getSibling();

        //No next question
        if (nextQuestion == null) {
            Log.d(TAG, String.format("'%s' -(sibling)-> null", currentQuestion.getCode()));
            return;
        }
        Log.d(TAG, String.format("'%s' -(sibling)-> '%s'", currentQuestion.getCode(),
                nextQuestion.getCode()));
        QuestionNode nextNode = buildNode(nextQuestion);
        currentNode.setSibling(nextNode);
        nextNode.setPreviousSibling(currentNode);
    }

    /**
     * Adds counters to this node
     */
    private void buildCounters(QuestionNode currentNode) {
        //precondition: options and some relations
        if (!withOptionsAndRelations(currentNode)) {
            return;
        }

        Question currentQuestion = currentNode.getQuestion();
        Answer currentAnswer = currentQuestion.getAnswer();
        for (Option option : currentAnswer.getOptions()) {
            Question optionCounter = currentQuestion.findCounterByOption(option);
            //no counter -> try next option
            if (optionCounter == null) {
                continue;
            }
            //found a counter -> annotate it
            currentNode.addCounter(option, optionCounter);
        }
    }

    /**
     * Checks if the currentNode requires knitting children, counters, ...
     */
    private boolean withOptionsAndRelations(QuestionNode currentNode) {
        Question currentQuestion = currentNode.getQuestion();
        //No children questions -> no children ||counters to build
        if (currentQuestion == null || !currentQuestion.hasOutputWithOptions()
                || currentQuestion.getQuestionOption().size() == 0) {
            return false;
        }

        //there might be something related to build
        return true;
    }

    /**
     * Annotates  the questions involved in this warning
     */
    private void annotateWarning(QuestionNode warningNode) {
        //Not a warning -> done
        if (warningNode.getQuestion().getOutput() != Constants.WARNING) {
            return;
        }

        WarningStatusChecker warningStatusChecker =
                (WarningStatusChecker) warningNode.getStatusChecker();
        Question questionWithThreshold = warningStatusChecker.getQuestionToSubscribeFromThreshold();
        Question questionWithOption = warningStatusChecker.getQuestionToSubscribeFromOption();

        addWarning(questionWithThreshold, warningNode);
        addWarning(questionWithOption, warningNode);
    }

    private void addWarning(Question subscriber, QuestionNode warningNode) {
        List<QuestionNode> warnings = this.warningsXQuestion.get(subscriber.getId_question());
        //First warning added
        if (warnings == null) {
            warnings = new ArrayList<>();
            this.warningsXQuestion.put(subscriber.getId_question(), warnings);
        }
        //Add new warning to list
        warnings.add(warningNode);
    }

    private void subscribeWarnings(QuestionNode subscriberNode) {
        List<QuestionNode> warnings = this.warningsXQuestion.get(
                subscriberNode.getQuestion().getId_question());
        //No warnings attached
        if (warnings == null) {
            return;
        }

        //Subscribe to every warning
        for (QuestionNode warningNode : warnings) {
            subscriberNode.addWarning(warningNode);
        }
    }

    public void setLoadBuildControllerListener(LoadBuildControllerListener listener) {
        mLoadBuildControllerListener = listener;
    }
}
