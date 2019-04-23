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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;

/**
 * A service that runs pushing process for pending surveys.
 */
public class PushService extends JobIntentService {

    /**
     * Constant added to the intent in order to reuse the service for different 'methods'
     */
    public static final String SERVICE_METHOD = "serviceMethod";

    /**
     * Name of 'push all pending surveys' action
     */
    public static final String PENDING_SURVEYS_ACTION =
            "org.eyeseetea.malariacare.services.PushService.PENDING_SURVEYS_ACTION";

    /**
     * Tag for logging
     */
    public static final String TAG = ".PushService";

    PushServiceStrategy mPushServiceStrategy = new PushServiceStrategy(this);

    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, PushService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //Ignore wrong actions
        if (!PENDING_SURVEYS_ACTION.equals(intent.getStringExtra(SERVICE_METHOD))) {
            return;
        }

        if(PreferencesState.getInstance().getContext()==null) {
            PreferencesState.getInstance().init(this);
        }

        mPushServiceStrategy.push();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onPushFinished() {
        reloadDashboard();
    }

    public void onPushError(String message) {
        PreferencesState.getInstance().setPushInProgress(false);
        Log.e(TAG, "onPushFinished error: " + message);
    }

    public static void reloadDashboard() {
        try{
            Intent surveysIntent = new Intent(PreferencesState.getInstance().getContext(),
                    SurveyService.class);
            surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
            PreferencesState.getInstance().getContext().startService(surveysIntent);
        } catch (Exception e){
            //From Android 8 versions, start services in background has limitations and
            //can throw errors. How SurveyService only has sense in foreground
            //simply catch error. On the future we should not use spervice to load surveys

            Log.d(TAG, "Error to start service to load surveys from background");
        }

        Intent broadcastIntent = new Intent(SurveyService.RELOAD_DASHBOARD_ACTION);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(broadcastIntent);
    }
}
