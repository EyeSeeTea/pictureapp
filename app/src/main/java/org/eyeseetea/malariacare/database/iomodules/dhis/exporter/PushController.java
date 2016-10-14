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

package org.eyeseetea.malariacare.database.iomodules.dhis.exporter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.SyncProgressStatus;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.network.ResponseHolder;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static controller that orchestrate the push process
 * Created by arrizabalaga on 4/11/15.
 */
public class PushController {
    private final String TAG=".PushController";

    private static PushController instance;

    /**
     * Context required to i18n error messages while pulling
     */
    private Context context;

    /**
     * The stateful converter used to turn a survey into its corresponding event + datavalues;
     */
    ConvertToSDKVisitor converter;


    public boolean isPushInProgress() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PreferencesState.getInstance().getContext());
        return sharedPreferences.getBoolean(PreferencesState.getInstance().getContext().getString(R.string.is_pushing_key), false);
    }

    public void setPushInProgress(boolean pushInProgress) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PreferencesState.getInstance().getContext());
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putBoolean(PreferencesState.getInstance().getContext().getResources().getString(R.string.is_pushing_key), pushInProgress); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }


    /**
     * Constructs and register this pull controller to the event bus
     */
    PushController(){
    }

    private void register(){
        Dhis2Application.bus.register(this);
    }

    /**
     * Unregister pull controller from bus events
     */
    private void unregister(){
        Dhis2Application.bus.unregister(this);
    }

    /**
     * Singleton constructor
     * @return
     */
    public static PushController getInstance(){
        if(instance==null){
            instance=new PushController();
        }
        return instance;
    }

    /**
     * Launches the push process
     * @param ctx
     */
    public void push(Context ctx,List<Survey> surveys){
        Log.d(TAG, "Starting PUSH process...");
        context=ctx;

        //No survey no push
        if(surveys==null || surveys.size()==0){
            PushController.getInstance().setPushInProgress(false);
            postException(new Exception(context.getString(R.string.progress_push_no_survey)));
            return;
        }


        Log.d("DpBlank", "Sets of Surveys to push");

        for (Survey srv : surveys){
            Log.d("DpBlank", "Survey to push " + srv.toString());
            for (Value dv : srv.getValues()){
                Log.d("DpBlank", "Values to push " + dv.toString());
            }
        }


        try {
            //Register for event bus
            register();

            //Converts app data into sdk events
            postProgress(context.getString(R.string.progress_push_preparing_survey));
            Log.d(TAG, "Preparing survey for pushing...");

            //delete all possible saved events before to conversion of surveys in events
            PopulateDB.wipeSDKData();

            convertToSDK(surveys);

            for(Survey survey:surveys){
                survey.setStatus(Constants.SURVEY_SENDING);
                survey.save();
            }

            //Asks sdk to push localdata
            postProgress(context.getString(R.string.progress_push_posting_survey));
            Log.d(TAG, "Pushing survey data to server...");
            DhisService.sendEventChanges();

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e(TAG,"push: "+ex.getLocalizedMessage());
            unregister();
            postException(ex);
            PushController.getInstance().setPushInProgress(false);
        }
    }

    @Subscribe
    public void onSendDataFinished(final NetworkJob.NetworkJobResult<Map<Long,ImportSummary>> result) {
        new Thread(){
            @Override
            public void run(){
                try {
                    if (result == null) {
                        Log.e(TAG, "onSendDataFinished with null");
                        return;
                    }

                    if(result!=null && result.getResourceType() != null && result.getResourceType().equals(ResourceType.USERS)) {
                        Log.e(TAG, "onSendDataFinished wrong subscribe(login)");
                        return;
                    }
                    //Error while pulling
                    if (result.getResponseHolder() != null && result.getResponseHolder().getApiException() != null) {
                        Log.e(TAG, result.getResponseHolder().getApiException().getMessage());
                        postException(new Exception(context.getString(R.string.dialog_pull_error)));
                        PushController.getInstance().setPushInProgress(false);
                        return;
                    }
                    //Ok: Updates + check ban server
                    postProgress(context.getString(R.string.progress_push_updating_survey));
                    Log.d(TAG, "Updating pushed survey data...");
                    converter.saveSurveyStatus(getImportSummaryMap(result));

                    Log.d(TAG, "Checking if server must be closed...");
                    ServerAPIController.banOrgUnitIfRequired();

                    Log.d(TAG, "PUSH process...OK");

                }catch (Exception ex){
                    Log.e(TAG,"onSendDataFinished: "+ex.getLocalizedMessage());
                    postException(ex);
                }finally {
                    postFinish();
                    unregister();
                    PushController.getInstance().setPushInProgress(false);
                }
            }
        }.start();
    }

    /**
     * Launches visitor that turns an APP survey into a SDK event
     */
    private void convertToSDK(List<Survey> surveys)throws  Exception{
        Log.d(TAG,"Converting APP survey into a SDK event");
        converter =new ConvertToSDKVisitor(context);
        for(Survey survey:surveys){
            Log.d(TAG,"Status of survey to be push is = "+survey.getStatus());
            survey.accept(converter);
        }
    }

    /**
     * Gets full importSummary for every Event that has been pushed to the server
     * @param result
     * @return
     */
    private Map<Long,ImportSummary> getImportSummaryMap(NetworkJob.NetworkJobResult<Map<Long,ImportSummary>> result){
        Map<Long,ImportSummary> emptyImportSummaryMap=new HashMap<>();
        //No result -> no details
        if(result==null){
            return emptyImportSummaryMap;
        }

        //General exception -> no details
        if (result.getResponseHolder() != null && result.getResponseHolder().getApiException() != null) {
            return emptyImportSummaryMap;
        }

        ResponseHolder<Map<Long,ImportSummary>> responseHolder=result.getResponseHolder();
        if(responseHolder==null || responseHolder.getItem()==null){
            return emptyImportSummaryMap;
        }

        return responseHolder.getItem();
    }

    /**
     * Notifies a progress into the bus (the caller activity will be listening)
     * @param msg
     */
    private void postProgress(String msg){
        Dhis2Application.getEventBus().post(new SyncProgressStatus(msg));
    }

    /**
     * Notifies an exception while pulling
     * @param ex
     */
    private void postException(Exception ex){
        ex.printStackTrace();
        Dhis2Application.getEventBus().post(new SyncProgressStatus(ex));
    }

    /**
     * Notifies that the pull is over
     */
    private void postFinish(){
        try {
            Log.i(TAG,"postFinish");
            Dhis2Application.getEventBus().post(new SyncProgressStatus());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
