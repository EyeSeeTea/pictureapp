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
import android.text.format.Time;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Survey$Table;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.monitor.MonitorBuilder;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A service that looks prepares monitor data in async manner
 * Created by arrizabalaga on 25/02/16.
 */
public class MonitorService extends IntentService {

    /**
     * Constant added to the intent in order to reuse the service for different 'methods'
     */
    public static final String SERVICE_METHOD="serviceMethod";

    /**
     * Name of 'prepare monitor' action
     */
    public static final String PREPARE_MONITOR_DATA ="org.eyeseetea.malariacare.services.MonitorService.PREPARE_MONITOR_DATA";

    /**
     * Tag for logging
     */
    public static final String TAG = ".MonitorService";

    /**
     * Constructor required due to a error message in AndroidManifest.xml if it is not present
     */
    public MonitorService(){
        super(MonitorService.class.getSimpleName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitorService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Bad action -> done
        if(!PREPARE_MONITOR_DATA.equals(intent.getStringExtra(SERVICE_METHOD))){
            return;
        }
        prepareMonitorData();
    }

    private void prepareMonitorData(){
        Log.i(TAG, "Preparing monitor data...");

        //Take last 6 months sent surveys
        List<Survey> sentSurveysForMonitor = SurveyMonitor.findSentSurveysForMonitor();

        Log.i(TAG, String.format("Found %d surveys to build monitor info, aggregating data...", sentSurveysForMonitor.size()));
        MonitorBuilder monitorBuilder = new MonitorBuilder(getApplicationContext());
        monitorBuilder.addSurveys(sentSurveysForMonitor);

        //Since intents does NOT admit NON serializable as values we use Session instead

        Log.i(TAG, String.format("Monitor data calculated ok",sentSurveysForMonitor.size()));
        Session.putServiceValue(PREPARE_MONITOR_DATA, monitorBuilder);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PREPARE_MONITOR_DATA));
    }

}
