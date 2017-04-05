/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.CompositeScore;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A service that looks for current Surveys to show on Dashboard(Details) in an asyn manner.
 * Created by arrizabalaga on 16/06/15.
 */
public class SurveyService extends IntentService {

    /**
     * Constant added to the intent in order to reuse the service for different 'methods'
     */
    public static final String SERVICE_METHOD = "serviceMethod";

    /**
     * Name of 'list unsent' action
     */
    public static final String ALL_UNSENT_SURVEYS_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.ALL_UNSENT_SURVEYS_ACTION";

    /**
     * Name of 'list uncompleted' action
     */
    public static final String ALL_UNCOMPLETED_SURVEYS_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.ALL_UNCOMPLETED_SURVEYS_ACTION";

    /**
     * Name of 'list completed' action
     */
    public static final String ALL_SENT_SURVEYS_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.ALL_SENT_SURVEYS_ACTION";

    /**
     * Name of 'remove list completed' action
     */
    public static final String REMOVE_SENT_SURVEYS_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.REMOVE_SENT_SURVEYS_ACTION";

    /**
     * Name of 'reload' action which returns both lists (unsent, sent)
     */
    public static final String RELOAD_DASHBOARD_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.RELOAD_DASHBOARD_ACTION";

    /**
     * Name of 'show' action
     */
    public static final String PREPARE_SURVEY_ACTION =
            "org.eyeseetea.malariacare.services.SurveyService.PREPARE_SURVEY_ACTION";

    /**
     * Key of composite scores entry in shared session
     */
    public static final String PREPARE_SURVEY_ACTION_COMPOSITE_SCORES =
            "org.eyeseetea.malariacare.services.SurveyService"
                    + ".PREPARE_SURVEY_ACTION_COMPOSITE_SCORES";

    /**
     * Key of tabs entry in shared session
     */
    public static final String PREPARE_SURVEY_ACTION_TABS =
            "org.eyeseetea.malariacare.services.SurveyService.PREPARE_SURVEY_ACTION_TABS";

    /**
     * Tag for logging
     */
    public static final String TAG = ".SurveyService";

    /**
     * Constructor required due to a error message in AndroidManifest.xml if it is not present
     */
    public SurveyService() {
        super(SurveyService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SurveyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Take action to be done
        switch (intent.getStringExtra(SERVICE_METHOD)) {
            case PREPARE_SURVEY_ACTION:
                prepareSurveyInfo();
                break;
            case ALL_UNSENT_SURVEYS_ACTION:
                getAllUnsentSurveys();
                break;
            case ALL_UNCOMPLETED_SURVEYS_ACTION:
                getAllUncompletedSurveys();
                break;
            case ALL_SENT_SURVEYS_ACTION:
                getAllSentSurveys();
                break;
            case REMOVE_SENT_SURVEYS_ACTION:
                removeAllSentSurveys();
                break;
            case RELOAD_DASHBOARD_ACTION:
                reloadDashboard();
                break;
        }
    }

    private void reloadDashboard() {
        Log.i(TAG, "reloadDashboard");
        List<Survey> surveys = Survey.getAllSurveys();

        List<Survey> unsentSurveys = Survey.getAllUnsentMalariaSurveys(
                new SurveyFragmentStrategy().getMalariaProgram());
        List<Survey> sentSurveys = Survey.getAllSentMalariaSurveys(
                new SurveyFragmentStrategy().getMalariaProgram());

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_UNSENT_SURVEYS_ACTION, unsentSurveys);
        Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, sentSurveys);

        //Returning result to anyone listening
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(ALL_UNSENT_SURVEYS_ACTION));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ALL_SENT_SURVEYS_ACTION));
    }

    /**
     * Selects all pending surveys from database
     */
    private void getAllUnsentSurveys() {
        Log.d(TAG, "getAllUnsentMalariaSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<Survey> surveys = Survey.getAllUnsentMalariaSurveys(
                new SurveyFragmentStrategy().getMalariaProgram());
        List<Survey> unsentSurveys = new ArrayList<Survey>();

        //Load %completion in every survey (it takes a while so it can NOT be done in UI Thread)
        for (Survey survey : surveys) {
            if (!survey.isSent() && !survey.isHide() && !survey.isConflict()) {
                survey.getAnsweredQuestionRatio();
                unsentSurveys.add(survey);
            }
        }

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_UNSENT_SURVEYS_ACTION, unsentSurveys);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(ALL_UNSENT_SURVEYS_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    /**
     * Selects all sent surveys from database
     */
    private void getAllSentSurveys() {
        Log.d(TAG, "getAllSentMalariaSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<Survey> surveys = Survey.getAllSentMalariaSurveys(
                new SurveyFragmentStrategy().getMalariaProgram());

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, surveys);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(ALL_SENT_SURVEYS_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    /**
     * Remove all sent surveys from database
     */
    private void removeAllSentSurveys() {
        Log.d(TAG, "removeAllSentSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select all sent surveys from sql and delete.
        List<Survey> surveys = Survey.getAllSentMalariaSurveys(
                new SurveyFragmentStrategy().getMalariaProgram());
        for (int i = surveys.size() - 1; i >= 0; i--) {
            //If is over limit the survey be delete, if is in the limit the survey change the
            // state to STATE_HIDE
            if (Utils.isDateOverLimit(Utils.DateToCalendar(surveys.get(i).getEventDate()), 1)) {
                surveys.get(i).delete();
            } else {
                surveys.get(i).setStatus(Constants.SURVEY_HIDE);
                surveys.get(i).save();
            }
        }
    }

    private void getAllUncompletedSurveys() {
        Log.d(TAG, "getAllUncompletedSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<Survey> surveys = Survey.getAllUncompletedSurveys();

        //Load %completion in every survey (it takes a while so it can NOT be done in UI Thread)
        for (Survey survey : surveys) {
            survey.getAnsweredQuestionRatio();
        }

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_UNCOMPLETED_SURVEYS_ACTION, surveys);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(ALL_UNCOMPLETED_SURVEYS_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    private void getAllCompletedSurveys() {
        Log.d(TAG, "getAllCompletedSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<Survey> surveys = Survey.getAllCompletedSurveys();

        //Load %completion in every survey (it takes a while so it can NOT be done in UI Thread)
        for (Survey survey : surveys) {
            survey.getAnsweredQuestionRatio();
        }

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, surveys);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(ALL_SENT_SURVEYS_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    /**
     * Prepares required data to show a survey completely (tabs and composite scores).
     */
    private void prepareSurveyInfo() {
        Log.d(TAG, "prepareSurveyInfo (Thread:" + Thread.currentThread().getId() + ")");

        //Get composite scores for current program & register them (scores)
        List<CompositeScore> compositeScores = new Select().from(
                CompositeScore.class).queryList();
        ScoreRegister.registerCompositeScores(compositeScores);

        //Get tabs for current program & register them (scores)
        List<Tab> tabs = new Select().from(Tab.class).queryList();
        ScoreRegister.registerTabScores(tabs);

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(PREPARE_SURVEY_ACTION_COMPOSITE_SCORES, compositeScores);
        Session.putServiceValue(PREPARE_SURVEY_ACTION_TABS, tabs);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(PREPARE_SURVEY_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }
}
