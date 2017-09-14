package org.eyeseetea.malariacare.strategies;


import android.app.Activity;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.services.SurveyService;

public class DashboardUnsentFragmentStrategy {
    public void registerSurveyReceiver(Activity activity,
            DashboardUnsentFragment.SurveyReceiver surveyReceiver) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(surveyReceiver,
                new IntentFilter(SurveyService.ALL_UNSENT_AND_SENT_SURVEYS_ACTION));
    }
}
