package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
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
     * Maps that holds the relationships between a QuestionDB and the warnings that it might trigger
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
    public void buildController(TabDB tabDB) throws LoadingNavigationControllerException{

        if (Session.isIsLoadingNavigationController()) {
            Log.d(TAG, "Navigation controller cannot load because it is already loading");
            return;
        }

        //No tab -> nothing to build
        if (tabDB == null) {
            Log.w(TAG, "Navigation controller cannot load because first tabDB doesn't exist");
            return;
        }

        warningsXQuestion.clear();

        Log.d(TAG, String.format("build(%s)", tabDB.getName()));
        final QuestionDB rootQuestionDB = QuestionDB.findRootQuestion(tabDB);

        //NO first question -> nothing to build
        if (rootQuestionDB == null) {
            Log.w(TAG, "Navigation controller cannot load because root question doesn't exist");
            return;
        }

        mAsyncExecutor.run(new Runnable() {
            @Override
            public void run() {
                buildNavigationController(rootQuestionDB);
            }
        });

    }

    private void buildNavigationController(QuestionDB rootQuestionDB){
        Log.d(TAG, "Begin loading navigation controller");

        try {
            Session.setIsLoadingNavigationController(true);
            //init steps counter
            step = 0;
            QuestionNode rootNode = buildNode(rootQuestionDB);

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
    private QuestionNode buildNode(QuestionDB currentQuestionDB) {
        //No question -> no node
        if (currentQuestionDB == null) {
            return null;
        }

        QuestionNode currentNode = new QuestionNode(currentQuestionDB);

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

        QuestionDB currentQuestionDB = currentNode.getQuestionDB();
        AnswerDB currentAnswerDB = currentQuestionDB.getAnswerDB();
        for (OptionDB optionDB : currentAnswerDB.getOptionDBs()) {
            QuestionDB firstChildrenQuestionDB = currentQuestionDB.findFirstChildrenByOption(optionDB);

            //No child question for this optionDB -> next
            if (firstChildrenQuestionDB == null) {
                continue;
            }

            Log.d(TAG, String.format("'%s' + '%s' --> '%s'", currentQuestionDB.getCode(),
                    optionDB.getCode(), firstChildrenQuestionDB.getCode()));
            //Build navigation from there
            QuestionNode childNode;
            //OptionDB that references self
            if (currentNode.getQuestionDB().getId_question()
                    == firstChildrenQuestionDB.getId_question()) {
                childNode = currentNode;
            } else {
                //Any other child question
                childNode = buildNode(firstChildrenQuestionDB);
            }

            //Add navigation by optionDB to current node
            currentNode.addNavigation(optionDB, childNode);
        }
    }

    /**
     * Adds navigation no matter what answer is given (sibling question)
     */
    private void buildSibling(QuestionNode currentNode) {
        QuestionDB currentQuestionDB = currentNode.getQuestionDB();
        QuestionDB nextQuestionDB = currentQuestionDB.getSibling();

        //No next question
        if (nextQuestionDB == null) {
            Log.d(TAG, String.format("'%s' -(sibling)-> null", currentQuestionDB.getCode()));
            return;
        }
        Log.d(TAG, String.format("'%s' -(sibling)-> '%s'", currentQuestionDB.getCode(),
                nextQuestionDB.getCode()));
        QuestionNode nextNode = buildNode(nextQuestionDB);
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

        QuestionDB currentQuestionDB = currentNode.getQuestionDB();
        AnswerDB currentAnswerDB = currentQuestionDB.getAnswerDB();
        for (OptionDB optionDB : currentAnswerDB.getOptionDBs()) {
            QuestionDB optionCounter = currentQuestionDB.findCounterByOption(optionDB);
            //no counter -> try next optionDB
            if (optionCounter == null) {
                continue;
            }
            //found a counter -> annotate it
            currentNode.addCounter(optionDB, optionCounter);
        }
    }

    /**
     * Checks if the currentNode requires knitting children, counters, ...
     */
    private boolean withOptionsAndRelations(QuestionNode currentNode) {
        QuestionDB currentQuestionDB = currentNode.getQuestionDB();
        //No children questions -> no children ||counters to build
        if (currentQuestionDB == null || !currentQuestionDB.hasOutputWithOptions()
                || currentQuestionDB.getQuestionOption().size() == 0) {
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
        if (warningNode.getQuestionDB().getOutput() != Constants.WARNING) {
            return;
        }

        WarningStatusChecker warningStatusChecker =
                (WarningStatusChecker) warningNode.getStatusChecker();
        QuestionDB questionDBWithThreshold = warningStatusChecker.getQuestionToSubscribeFromThreshold();
        QuestionDB questionDBWithOption = warningStatusChecker.getQuestionToSubscribeFromOption();

        addWarning(questionDBWithThreshold, warningNode);
        addWarning(questionDBWithOption, warningNode);
    }

    private void addWarning(QuestionDB subscriber, QuestionNode warningNode) {
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
                subscriberNode.getQuestionDB().getId_question());
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
