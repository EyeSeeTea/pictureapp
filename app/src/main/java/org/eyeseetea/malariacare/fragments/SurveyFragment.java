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
package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SurveyFragment extends Fragment implements IDashboardFragment {

    public static final String TAG = ".SurveyActivity";
    /**
     * Progress text shown while loading
     */
    public static CustomTextView progressText;
    public static Iterator<String> messageIterator;
    public static int messagesCount;
    public boolean mReviewMode = false;
    /**
     * Actual layout to be accessible in the fragment
     */
    RelativeLayout llLayout;

    private DynamicTabAdapter dynamicTabAdapter;

    /**
     * Receiver of data from SurveyService
     */
    private SurveyReceiver surveyReceiver;
    /**
     * Progress dialog shown while loading
     */
    private ProgressBar progressBar;
    /**
     * Parent view of main content
     */
    private LinearLayout content;

    public static void nextProgressMessage() {
        DashboardActivity.dashboardActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (messageIterator.hasNext()) {
                    progressText.setText(messageIterator.next());
                }
            }
        });
    }

    public static int progressMessagesCount() {
        return messagesCount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        prepareSurveyInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }

        llLayout = (RelativeLayout) inflater.inflate(R.layout.survey, container, false);
        registerFragmentReceiver();
        createProgress();
        return llLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (Session.getMalariaSurvey() != null) {
            Session.getMalariaSurvey().getValuesFromDB();
        }
        if (Session.getStockSurvey() != null) {
            Session.getStockSurvey().getValuesFromDB();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (!DashboardActivity.dashboardActivity.isLoadingReview()
                && !areActiveSurveysInQuarantine()) {
            beforeExit();
        }
        super.onPause();
    }

    private boolean areActiveSurveysInQuarantine() {
        Survey survey = Session.getMalariaSurvey();
        if (survey != null && survey.isQuarantine()) {
            return true;
        }
        survey = Session.getStockSurvey();
        if (survey != null && survey.isQuarantine()) {
            return true;
        }

        return false;
    }

    private void beforeExit() {
        DashboardActivity.dashboardActivity.beforeExit();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        unregisterFragmentReceiver();
        super.onStop();
    }

    /**
     * Gets a reference to the progress view in order to stop it later
     */
    private void createProgress() {
        content = (LinearLayout) llLayout.findViewById(R.id.content);
        progressBar = (ProgressBar) llLayout.findViewById(R.id.survey_progress);
        progressText = (CustomTextView) llLayout.findViewById(R.id.progress_text);
        createProgressMessages();
    }

    private void createProgressMessages() {
        List<String> messagesList = new ArrayList<>();
        //// FIXME: 20/03/2017 it is a fake flow.
        messagesList.add(
                PreferencesState.getInstance().getContext().getString(R.string.survey_first_step));
        messagesList.add(
                PreferencesState.getInstance().getContext().getString(R.string.survey_second_step));
        messagesList.add(
                PreferencesState.getInstance().getContext().getString(R.string.survey_third_step));
        messagesList.add(
                PreferencesState.getInstance().getContext().getString(R.string.survey_fourth_step));
        messageIterator = messagesList.iterator();
        // An iterator doesn't return properly its size after next() has been called
        messagesCount = messagesList.size();
    }

    /**
     * Prepares the selected tab to be shown
     */
    private View prepareTab(Tab selectedTab) {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());

        dynamicTabAdapter = new DynamicTabAdapter(selectedTab, getActivity(), mReviewMode);

        return inflater.inflate(dynamicTabAdapter.getLayout(), content, false);
    }

    /**
     * Stops progress view and shows real form
     */
    private void stopProgress() {
        this.progressBar.setVisibility(View.GONE);
        this.progressText.setVisibility(View.GONE);
        this.content.setVisibility(View.VISIBLE);

    }

    private void startProgress() {
        this.content.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);
        this.progressBar.setEnabled(true);
        this.progressText.setVisibility(View.VISIBLE);
    }

    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    public void registerFragmentReceiver() {
        Log.d(TAG, "registerFragmentReceiver");

        if (surveyReceiver == null) {
            surveyReceiver = new SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver,
                    new IntentFilter(SurveyService.PREPARE_SURVEY_ACTION));
        }
    }

    /**
     * Unregisters the survey receiver.
     * It really important to do this, otherwise each receiver will invoke its code.
     */
    public void unregisterFragmentReceiver() {
        Log.d(TAG, "unregisterFragmentReceiver");
        if (surveyReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(surveyReceiver);
            surveyReceiver = null;
        }
    }

    /**
     * Asks SurveyService for the current list of surveys
     */
    public void prepareSurveyInfo() {
        Log.d(TAG, "prepareSurveyInfo");
        Intent surveysIntent = new Intent(getActivity().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.PREPARE_SURVEY_ACTION);
        getActivity().getApplicationContext().startService(surveysIntent);
    }

    @Override
    public void reloadData() {

    }

    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
    }

    public class AsyncReloadAdaptersAndChangeTab extends AsyncTask<Void, Integer, View> {

        private List<Tab> tabs;

        public AsyncReloadAdaptersAndChangeTab(List<Tab> tabs) {
            this.tabs = tabs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
        }

        @Override
        protected View doInBackground(Void... params) {
            Log.d(TAG, "doInBackground(" + Thread.currentThread().getId() + ")..");
            View view = prepareTab(tabs.get(0));
            Log.d(TAG, "doInBackground(" + Thread.currentThread().getId() + ")..DONE");
            return view;
        }

        @Override
        protected void onPostExecute(View viewContent) {
            super.onPostExecute(viewContent);

            content.removeAllViews();
            content.addView(viewContent);

            ListView listViewTab = (ListView) llLayout.findViewById(R.id.listView);

            dynamicTabAdapter.addOnSwipeListener(listViewTab);

            listViewTab.setAdapter(dynamicTabAdapter);

            stopProgress();
        }
    }

    /**
     * Inner private class that receives the result from the service
     */
    private class SurveyReceiver extends BroadcastReceiver {
        private SurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //FIXME: 09/03/2017  Refactor: This is used to prevent multiple open and close
            // surveys crash
            Session.setIsLoadingSurvey(true);

            List<Tab> tabs;

            Session.valuesLock.readLock().lock();
            try {
                tabs = (List<Tab>) Session.popServiceValue(
                        SurveyService.PREPARE_SURVEY_ACTION_TABS);
            } finally {
                Session.valuesLock.readLock().unlock();
            }

            new AsyncReloadAdaptersAndChangeTab(tabs)
                    .execute((Void) null);
        }
    }
}
