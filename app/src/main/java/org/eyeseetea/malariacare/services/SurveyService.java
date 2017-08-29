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

import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.CompositeScoreDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.usecase.GetUserProgramUIDUseCase;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

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
     * The user program UID
     */
    private String mProgramUID;

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
            case RELOAD_DASHBOARD_ACTION:
                reloadDashboard();
                break;
        }
    }

    private void reloadDashboard() {
        Log.i(TAG, "reloadDashboard");
        getProgramUID(new Callback() {
            @Override
            public void onSuccess(String uid) {
                List<SurveyDB> unsentSurveyDBs = SurveyDB.getAllUnsentMalariaSurveys(uid);
                List<SurveyDB> sentSurveyDBs = SurveyDB.getAllSentMalariaSurveys(uid);

                //Since intents does NOT admit NON serializable as values we use Session instead
                Session.putServiceValue(ALL_UNSENT_SURVEYS_ACTION, unsentSurveyDBs);
                Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, sentSurveyDBs);

                //Returning result to anyone listening
                LocalBroadcastManager.getInstance(SurveyService.this).sendBroadcast(
                        new Intent(ALL_UNSENT_SURVEYS_ACTION));
                LocalBroadcastManager.getInstance(SurveyService.this).sendBroadcast(
                        new Intent(ALL_SENT_SURVEYS_ACTION));
            }
        });
    }

    /**
     * Selects all pending surveys from database
     */
    private void getAllUnsentSurveys() {
        Log.d(TAG, "getAllUnsentMalariaSurveys (Thread:" + Thread.currentThread().getId() + ")");
//Select surveys from sql
        getProgramUID(new Callback() {
            @Override
            public void onSuccess(String uid) {
                List<SurveyDB> surveyDBs = SurveyDB.getAllUnsentMalariaSurveys(uid);
                List<SurveyDB> unsentSurveyDBs = new ArrayList<SurveyDB>();

                //Load %completion in every survey (it takes a while so it can NOT be done in UI
                // Thread)
                for (SurveyDB surveyDB : surveyDBs) {
                    if (!surveyDB.isSent() && !surveyDB.isConflict()) {
                        surveyDB.getAnsweredQuestionRatio();
                        unsentSurveyDBs.add(surveyDB);
                    }
                }

                //Since intents does NOT admit NON serializable as values we use Session instead
                Session.putServiceValue(ALL_UNSENT_SURVEYS_ACTION, unsentSurveyDBs);

                //Returning result to anyone listening
                Intent resultIntent = new Intent(ALL_UNSENT_SURVEYS_ACTION);
                LocalBroadcastManager.getInstance(SurveyService.this).sendBroadcast(resultIntent);
            }
        });
    }

    /**
     * Selects all sent surveys from database
     */
    private void getAllSentSurveys() {
        Log.d(TAG, "getAllSentMalariaSurveys (Thread:" + Thread.currentThread().getId() + ")");
        getProgramUID(new Callback() {
            @Override
            public void onSuccess(String uid) {
                //Select surveys from sql
                List<SurveyDB> surveyDBs = SurveyDB.getAllSentMalariaSurveys(uid);

                //Since intents does NOT admit NON serializable as values we use Session instead
                Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, surveyDBs);

                //Returning result to anyone listening
                Intent resultIntent = new Intent(ALL_SENT_SURVEYS_ACTION);
                LocalBroadcastManager.getInstance(SurveyService.this).sendBroadcast(resultIntent);
            }
        });
    }

    private void getAllUncompletedSurveys() {
        Log.d(TAG, "getAllUncompletedSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<SurveyDB> surveyDBs = SurveyDB.getAllUncompletedSurveys();

        //Load %completion in every survey (it takes a while so it can NOT be done in UI Thread)
        for (SurveyDB surveyDB : surveyDBs) {
            surveyDB.getAnsweredQuestionRatio();
        }

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_UNCOMPLETED_SURVEYS_ACTION, surveyDBs);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(ALL_UNCOMPLETED_SURVEYS_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    private void getAllCompletedSurveys() {
        Log.d(TAG, "getAllCompletedSurveys (Thread:" + Thread.currentThread().getId() + ")");

        //Select surveys from sql
        List<SurveyDB> surveyDBs = SurveyDB.getAllCompletedSurveys();

        //Load %completion in every survey (it takes a while so it can NOT be done in UI Thread)
        for (SurveyDB surveyDB : surveyDBs) {
            surveyDB.getAnsweredQuestionRatio();
        }

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(ALL_SENT_SURVEYS_ACTION, surveyDBs);

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
        List<CompositeScoreDB> compositeScoreDBs = new Select().from(
                CompositeScoreDB.class).queryList();
        ScoreRegister.registerCompositeScores(compositeScoreDBs);

        //Get tabs for current program & register them (scores)
        List<TabDB> tabDBs = new Select().from(TabDB.class).queryList();
        ScoreRegister.registerTabScores(tabDBs);

        //Since intents does NOT admit NON serializable as values we use Session instead
        Session.putServiceValue(PREPARE_SURVEY_ACTION_COMPOSITE_SCORES, compositeScoreDBs);
        Session.putServiceValue(PREPARE_SURVEY_ACTION_TABS, tabDBs);

        //Returning result to anyone listening
        Intent resultIntent = new Intent(PREPARE_SURVEY_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }


    private void getProgramUID(final Callback callback) {
        if (mProgramUID != null) {
            callback.onSuccess(mProgramUID);
        } else {
            IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
            IMainExecutor mainExecutor = new UIThreadExecutor();
            IAsyncExecutor asyncExecutor = new AsyncExecutor();
            GetUserProgramUIDUseCase getUserProgramUIDUseCase = new GetUserProgramUIDUseCase(
                    programLocalDataSource, mainExecutor, asyncExecutor);
            getUserProgramUIDUseCase.execute(new GetUserProgramUIDUseCase.Callback() {
                @Override
                public void onSuccess(String uid) {
                    mProgramUID = uid;
                    callback.onSuccess(uid);
                }

                @Override
                public void onError() {
                    Log.e(TAG, "error getting user program");
                }
            });
        }
    }

    private interface Callback {
        void onSuccess(String uid);
    }
}
