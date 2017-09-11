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

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;

/**
 * A service that runs pushing process for pending surveys.
 */
public class PushService extends IntentService {

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

    /**
     * Constructor required due to a error message in AndroidManifest.xml if it is not present
     */
    public PushService() {
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

    public static void reloadDashboard() {
        Intent surveysIntent = new Intent(PreferencesState.getInstance().getContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().startService(surveysIntent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Ignore wrong actions
        if (!PENDING_SURVEYS_ACTION.equals(intent.getStringExtra(SERVICE_METHOD))) {
            return;
        }
        mPushServiceStrategy.push();
    }

    public void onPushFinished() {
        reloadDashboard();
    }

    public void onPushError(String message) {
        PreferencesState.getInstance().setPushInProgress(false);
        Log.w(TAG, "onPushFinished error: " + message);
    }

}
