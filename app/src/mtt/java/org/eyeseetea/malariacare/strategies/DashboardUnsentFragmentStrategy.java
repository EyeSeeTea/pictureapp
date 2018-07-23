package org.eyeseetea.malariacare.strategies;


import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.Date;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy {
    public DashboardUnsentFragmentStrategy(
            DashboardUnsentFragment dashboardUnsentFragment) {
        super(dashboardUnsentFragment);
    }

    public void registerSurveyReceiver(Activity activity,
            DashboardUnsentFragment.SurveyReceiver surveyReceiver) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(surveyReceiver,
                new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
    }

    @Override
    public void deleteSurvey(SurveyDB surveyDB) {
        Context context = PreferencesState.getInstance().getContext();
        Date date = surveyDB.getEventDate();
        surveyDB.delete();
        SurveyDB stockSurvey = SurveyDB.findSurveysWithProgramAndEventDate(
                context.getResources().getString(R.string.stock_program_uid), date);
        if(stockSurvey!=null) {
            stockSurvey.delete();
        }
    }
}
