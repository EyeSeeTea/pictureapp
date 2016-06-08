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
import android.util.Log;
import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.SyncProgressStatus;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.PushResult;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.util.ArrayList;
import java.util.List;

/**
 * A service that runs pushing process for pending surveys.
 * Created by rhardjon on 19/09/15.
 */
public class PushService extends IntentService {

    /**
     * Constant added to the intent in order to reuse the service for different 'methods'
     */
    public static final String SERVICE_METHOD="serviceMethod";

    /**
     * Name of 'push all pending surveys' action
     */
    public static final String PENDING_SURVEYS_ACTION ="org.eyeseetea.malariacare.services.PushService.PENDING_SURVEYS_ACTION";

    /**
     * List of surveys that are going to be pushed
     */
    List<Survey> surveys;

    /**
     * Tag for logging
     */
    public static final String TAG = ".PushService";



    /**
     * Constructor required due to a error message in AndroidManifest.xml if it is not present
     */
    public PushService(){
        super(PushService.class.getSimpleName());
        Log.d(TAG, "PushService() register in Dhis2Application bus");
    }

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PushService(String name) {
        super(name);
        Log.d(TAG, "PushService(name) constructor");
    }

    private synchronized void startProgress(){
        Log.d(TAG, "startProgress, registering in bus");
        Dhis2Application.bus.register(this);
    }

    private synchronized void stopProgress(){
        Log.d(TAG, "stopProgress, unregistering from bus");
        Dhis2Application.bus.unregister(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Ignore wrong actions
        if(!PENDING_SURVEYS_ACTION.equals(intent.getStringExtra(SERVICE_METHOD))){
            return;
        }

        Log.d("DpBlank", "Push in Progress" + PushController.getInstance().isPushInProgress());

        if (PushController.getInstance().isPushInProgress()){
            return;
        }

        //Launch push according to current server
        pushAllPendingSurveys();
    }

    /**
     * Push all pending surveys
     */
    private void pushAllPendingSurveys() {
        Log.d(TAG, "pushAllPendingSurveys (Thread:" + Thread.currentThread().getId() + ")");

        PushController.getInstance().setPushInProgress(true);

        //Fixme the method getAllUnsentSurveys returns all the surveys not sent(completed, inprogres, and hide)
        //Select surveys from sql
        surveys = Survey.getAllSurveysToBeSent();

        //No surveys to send -> done
        if(surveys==null || surveys.isEmpty()){
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        //Server is not ready for push -> move on
        if(!ServerAPIController.isReadyForPush()){
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        //Push according to current server version
        if(ServerAPIController.isAPIServer()){
            pushByAPI();
        }else{
            pushBySDK();
        }

    }

    /**
     * Pushes pending surveys via API
     */
    private void pushByAPI(){
        Log.i(TAG, "pushByAPI");
        PushClient pushClient=new PushClient(getApplicationContext());
        for(Survey survey : surveys){
            //Prepare for sending current survey
            pushClient.setSurvey(survey);

            //Push  data
            PushResult result = pushClient.pushBackground();
            if(result.isSuccessful()){
                Log.d(TAG, "pushByAPI ok");
                reloadDashboard();
            }else{
                Log.e(TAG, "pushByAPI ERROR");
            }
        }
    }

    /**
     * Push via sdk requires 2 steps:
     *  -Login into sdk
     *  -Push data via PushController
     */
    private void pushBySDK(){
        Log.i(TAG, "pushBySDK");
        startProgress();

        //Init sdk login
        DhisService.logInUser(HttpUrl.parse(ServerAPIController.getServerUrl()), ServerAPIController.getSDKCredentials());
    }

    @Subscribe
    public void callbackLoginPrePush(NetworkJob.NetworkJobResult<ResourceType> result) {
        Log.d(TAG, "callbackLoginPrePush");
        //Nothing to check
        if(result==null || result.getResourceType()==null || !result.getResourceType().equals(ResourceType.USERS)){
            return;
        }

        //Login failed
        if(result.getResponseHolder().getApiException()!=null) {
            Log.e(TAG, "callbackLoginPrePush cannot login via sdk");
            stopProgress();
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        callPushBySDK();
    }

    private void callPushBySDK(){

        List<Survey> filteredSurveys = new ArrayList<>();
        //Check surveys not in progress
        for (Survey survey: surveys){

            if (survey.isCompleted(survey.getId_survey()) && survey.getValues().size() > 0){
                Log.d("DpBlank", "Survey is completed" + survey.getId_survey());
                filteredSurveys.add(survey);
            }
            else{
                Log.d("DpBlank", "Survey is sent" + survey.getId_survey());
            }
        }

        if (filteredSurveys.size()==0){
            stopProgress();
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        //Login successful start reload
        PushController.getInstance().push(getApplicationContext(), filteredSurveys);

    }

    /**
     * Callback that is invoked once the push is over or has failed
     * @param syncProgressStatus
     */
    @Subscribe
    public void onPushBySDKFinished(final SyncProgressStatus syncProgressStatus) {
        Log.d(TAG, "onPushBySDKFinished ");
        if(syncProgressStatus ==null){
            Log.i(TAG, "onPushBySDKFinished null");
            stopProgress();
            return;
        }

        //Step
        if (syncProgressStatus.hasProgress()) {
            Log.i(TAG, "onPushBySDKFinished progress: " + syncProgressStatus.getMessage());
            return;
        }

        //Exception
        if (syncProgressStatus.hasError()) {
            Log.w(TAG, "onPushBySDKFinished error: " + syncProgressStatus.getException().getMessage());
            stopProgress();
            return;
        }

        //Finish
        if (syncProgressStatus.isFinish()) {
            Log.i(TAG, "onPushBySDKFinished finished");
            reloadDashboard();
            stopProgress();
        }
    }

    /**
     * Reloads dashboard via SurveyService
     */
    private void reloadDashboard(){
        Intent surveysIntent=new Intent(this, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        this.startService(surveysIntent);
    }
}
