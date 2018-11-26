package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.List;

public abstract class ADashboardUnsentFragmentStrategy {
    DashboardUnsentFragment mDashboardUnsentFragment;

    public ADashboardUnsentFragmentStrategy(
            DashboardUnsentFragment dashboardUnsentFragment) {
        mDashboardUnsentFragment = dashboardUnsentFragment;
    }

    public void registerSurveyReceiver(Activity activity,
            DashboardUnsentFragment.SurveyReceiver surveyReceiver) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(surveyReceiver,
                new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
    }

    public void saveBundle(Bundle outState) {

    }

    public View inflateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unsent_list_fragment, container, false);
    }

    public void manageOnItemClick(AssessmentAdapter adapter, ListView l,
            int position, List<SurveyDB> surveyDBs) {
        adapter.onClick(l, position, surveyDBs);
    }

    public void reloadHeader(Activity activity) {
        mDashboardUnsentFragment.reloadHeader(activity, R.string.tab_tag_assess);
    }

    public void reloadData() {
        //Reload data using service
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }

    public void onReceiveSurveys(Intent intent) {

    }

    public void deleteSurvey(SurveyDB surveyDB){
        surveyDB.delete();
    }
}
