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
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.List;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy implements
        View.OnClickListener {
    public static final String IS_STOCK_FRAGMENT = "isStockFragment";

    private boolean isStockFragment;

    public DashboardUnsentFragmentStrategy(
            DashboardUnsentFragment dashboardUnsentFragment) {
        super(dashboardUnsentFragment);
    }

    @Override
    public void registerSurveyReceiver(Activity activity,
            DashboardUnsentFragment.SurveyReceiver surveyReceiver) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(surveyReceiver,
                new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
    }

    @Override
    public void saveBundle(Bundle outState) {
        super.saveBundle(outState);
        outState.putBoolean(IS_STOCK_FRAGMENT, isStockFragment);
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = mDashboardUnsentFragment.getArguments();
        }
        if (bundle != null) {
            isStockFragment = bundle.getBoolean(IS_STOCK_FRAGMENT, false);
        }
        if (isStockFragment) {
            View view = inflater.inflate(R.layout.stock_list_fragemnt, container, false);
            view.findViewById(R.id.add_stock_survey).setOnClickListener(this);
            return view;
        } else {
            return super.inflateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_stock_survey:
                showNewStockSurvey();
                break;
        }
    }

    private void showNewStockSurvey() {

    }

    @Override
    public void manageOnItemClick(AssessmentAdapter adapter, ListView l, int position,
            List<SurveyDB> surveyDBs) {
        if (!isStockFragment) {
            super.manageOnItemClick(adapter, l, position, surveyDBs);
        }
    }

    @Override
    public void reloadHeader(Activity activity) {
        if (isStockFragment) {
            mDashboardUnsentFragment.reloadHeader(activity, R.string.tab_stock);
        } else {
            super.reloadHeader(activity);
        }
    }

    @Override
    public void reloadData() {
        if (isStockFragment) {
            //Reload data using service
            Intent surveysIntent = new Intent(
                    PreferencesState.getInstance().getContext().getApplicationContext(),
                    SurveyService.class);
            surveysIntent.putExtra(SurveyService.SERVICE_METHOD,
                    SurveyService.GET_SURVEYS_FROM_PROGRAM);
            surveysIntent.putExtra(SurveyService.PROGRAM_UID,
                    mDashboardUnsentFragment.getActivity().getString(R.string.stock_program_uid));
            PreferencesState.getInstance().getContext().getApplicationContext().startService(
                    surveysIntent);
        } else {
            super.reloadData();
        }
    }

    @Override
    public void onReceiveSurveys(Intent intent) {
        super.onReceiveSurveys(intent);
        if (SurveyService.GET_SURVEYS_FROM_PROGRAM.equals(intent.getAction())) {
            List<SurveyDB> surveysUnsentFromService;
            Session.valuesLock.readLock().lock();
            try {
                surveysUnsentFromService = (List<SurveyDB>) Session.popServiceValue(
                        SurveyService.GET_SURVEYS_FROM_PROGRAM);
            } finally {
                Session.valuesLock.readLock().unlock();
            }
            mDashboardUnsentFragment.reloadSurveysFromService(surveysUnsentFromService);
        }
    }
}
