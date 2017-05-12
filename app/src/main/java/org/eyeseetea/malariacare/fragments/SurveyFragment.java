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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.CompositeScore;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.ITabAdapter;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity that supports the data entry for the surveys.
 */
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
    /**
     * List of tabs that belongs to the current selected survey
     */
    private List<Tab> tabsList = new ArrayList<>();
    private TabAdaptersCache tabAdaptersCache = new TabAdaptersCache();
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
    /**
     * Flags required to decide if the survey must be deleted or not
     */
    private boolean isBackPressed = false;

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
     * Finds the option from the current answer associated with the given text.
     * Only for dynamicTabAdapter, required for automated testing.
     */
    public Option findOptionByText(String text) {
        try {
            //Find adapter
            Tab tabZero = this.tabsList.get(0);
            DynamicTabAdapter tabAdapter = (DynamicTabAdapter) this.tabAdaptersCache.findAdapter(
                    tabZero);

            //Get options from question
            List<Option> options =
                    tabAdapter.navigationController.getCurrentQuestion().getAnswer().getOptions();

            //Return proper option if possible
            for (Option option : options) {
                if (option.getName().equals(text)) {
                    return option;
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
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

        if (selectedTab.isCompositeScore()) {
            //Initialize scores x question not loaded yet
            List<Tab> notLoadedTabs = tabAdaptersCache.getNotLoadedTabs();
            ScoreRegister.initScoresForQuestions(Question.listAllByTabs(notLoadedTabs),
                    Session.getMalariaSurvey());
        }
        ITabAdapter tabAdapter = tabAdaptersCache.findAdapter(selectedTab);

        return inflater.inflate(tabAdapter.getLayout(), content, false);
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
        private List<CompositeScore> compositeScores;

        public AsyncReloadAdaptersAndChangeTab(List<Tab> tabs,
                List<CompositeScore> compositeScores) {
            this.tabs = tabs;
            this.compositeScores = compositeScores;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
        }

        @Override
        protected View doInBackground(Void... params) {
            tabsList.clear();
            tabsList.addAll(tabs);
            tabAdaptersCache.reloadAdapters(tabs, compositeScores);

            Log.d(TAG, "doInBackground(" + Thread.currentThread().getId() + ")..");
            View view = prepareTab(tabs.get(0));
            Log.d(TAG, "doInBackground(" + Thread.currentThread().getId() + ")..DONE");
            return view;
        }

        @Override
        protected void onPostExecute(View viewContent) {
            super.onPostExecute(viewContent);

            Tab tab = tabs.get(0);

            content.removeAllViews();
            content.addView(viewContent);
            ITabAdapter tabAdapter = tabAdaptersCache.findAdapter(tab);

            ListView listViewTab = (ListView) llLayout.findViewById(R.id.listView);
            if (tabAdapter instanceof DynamicTabAdapter) {
                ((DynamicTabAdapter) tabAdapter).addOnSwipeListener(listViewTab);
            }
            listViewTab.setAdapter((BaseAdapter) tabAdapter);
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
            List<CompositeScore> compositeScores;
            List<Tab> tabs;
            Session.valuesLock.readLock().lock();
            try {
                compositeScores = (List<CompositeScore>) Session.popServiceValue(
                        SurveyService.PREPARE_SURVEY_ACTION_COMPOSITE_SCORES);
            } finally {
                Session.valuesLock.readLock().unlock();
            }
            Session.valuesLock.readLock().lock();
            try {
                tabs = (List<Tab>) Session.popServiceValue(
                        SurveyService.PREPARE_SURVEY_ACTION_TABS);
            } finally {
                Session.valuesLock.readLock().unlock();
            }

            new AsyncReloadAdaptersAndChangeTab(tabs, compositeScores)
                    .execute((Void) null);
        }
    }

    /**
     * Inner class that resolves each Tab as it is required (lazy manner) instead of loading all of
     * them at once.
     */
    private class TabAdaptersCache {

        /**
         * Cache of {tab: adapter} for each tab in the survey
         */
        private Map<Tab, ITabAdapter> adapters = new HashMap<Tab, ITabAdapter>();

        /**
         * List of composite scores of the current survey
         */
        private List<CompositeScore> compositeScores;

        /**
         * Flag that optimizes the load of compositeScore the next time
         */
        private boolean compositeScoreTabShown = false;

        /**
         * Finds the right adapter according to the selected tab.
         * Tabs are lazy trying to speed up the first load
         *
         * @param tab Tab whose adapter is searched.
         * @return The right adapter to deal with that Tab
         */
        public ITabAdapter findAdapter(Tab tab) {
            ITabAdapter adapter = adapters.get(tab);
            if (adapter == null) {
                adapter = buildAdapter(tab);
                //The 'Score' tab has no adapter
                if (adapter != null) {
                    this.adapters.put(tab, adapter);
                }
            }
            return adapter;
        }

        public List<Tab> getNotLoadedTabs() {
            List<Tab> notLoadedTabs = new ArrayList<Tab>();
            //If has already been shown NOTHING to reload
            if (compositeScoreTabShown) {
                return notLoadedTabs;
            }

            compositeScoreTabShown = true;
            notLoadedTabs = new ArrayList<>(tabsList);
            Set<Tab> loadedTabs = adapters.keySet();
            notLoadedTabs.removeAll(loadedTabs);
            return notLoadedTabs;
        }

        /**
         * Resets the state of the cache.
         * Called form the receiver once data is ready.
         */
        public void reloadAdapters(List<Tab> tabs, List<CompositeScore> compositeScores) {
            Tab firstTab = tabs.get(0);
            this.adapters.clear();
            this.adapters.put(firstTab, buildAdapter(firstTab));
            this.compositeScores = compositeScores;
        }

        /**
         * Returns the list of adapters.
         * Puts every adapter (for every tab) into the cache if is not already there.
         */
        public List<ITabAdapter> list() {
            //The cache only has loaded Tabs
            if (this.adapters.size() < tabsList.size()) {
                cacheAllTabs();
            }
            //Return full list of adapters
            return new ArrayList<ITabAdapter>(this.adapters.values());

        }

        /**
         * Puts every adapter (for every tab) into the cache if is not already there.
         */
        public void cacheAllTabs() {
            for (Tab tab : tabsList) {
                findAdapter(tab);
            }
        }

        /**
         * Builds the right adapter for the given tab
         */
        private ITabAdapter buildAdapter(Tab tab) {
            if (tab.isDynamicTab() || tab.isMultiQuestionTab()) {
                return new DynamicTabAdapter(tab, getActivity(), mReviewMode);
            }

            return null;
        }
    }
}
