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
import android.os.Bundle;
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
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.exception.LoadingSurveyException;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SurveyFragment extends Fragment {

    public static final String TAG = ".SurveyFragment";
    /**
     * Progress text shown while loading
     */
    public static CustomTextView progressText;
    public static Iterator<String> messageIterator;
    public static int messagesCount = 4;
    public boolean mReviewMode = false;
    /**
     * Actual layout to be accessible in the fragment
     */
    RelativeLayout llLayout;

    private DynamicTabAdapter dynamicTabAdapter;

    /**
     * Progress dialog shown while loading
     */
    private ProgressBar progressBar;
    /**
     * Parent view of main content
     */
    private LinearLayout content;

    public static void nextProgressMessage() {
        if (DashboardActivity.dashboardActivity != null) {
            DashboardActivity.dashboardActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (messageIterator != null) {
                        if (messageIterator.hasNext()) {
                            progressText.setText(messageIterator.next());
                        }
                    }
                }
            });
        }
    }

    public static int progressMessagesCount() {
        return messagesCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (container == null) {
            return null;
        }

        llLayout = (RelativeLayout) inflater.inflate(R.layout.survey, container, false);

        createProgress();
        initializeSurvey();

        return llLayout;
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
    }


    /**
     * Stops progress view and shows real form
     */
    private void hideProgress() {
        this.progressBar.setVisibility(View.GONE);
        this.progressText.setVisibility(View.GONE);
        this.content.setVisibility(View.VISIBLE);

    }

    private void showProgress() {
        this.content.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);
        this.progressBar.setEnabled(true);
        this.progressText.setVisibility(View.VISIBLE);
    }

    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    public void initializeSurvey() {
        showProgress();

        if (!Session.isIsLoadingNavigationController()) {
            showSurvey();
        } else {
            NavigationBuilder.getInstance().setLoadBuildControllerListener(
                    new NavigationBuilder.LoadBuildControllerListener() {
                        @Override
                        public void onLoadFinished() {
                            showSurvey();
                        }
                    });
        }
    }

    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
    }

    private void showSurvey() {
        try {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());

            dynamicTabAdapter = new DynamicTabAdapter(getActivity(), mReviewMode);

            View viewContent = inflater.inflate(dynamicTabAdapter.getLayout(), content, false);

            content.removeAllViews();
            content.addView(viewContent);

            ListView listViewTab = (ListView) llLayout.findViewById(R.id.listView);

            dynamicTabAdapter.addOnSwipeListener(listViewTab);

            listViewTab.setAdapter(dynamicTabAdapter);

            hideProgress();
        }catch (NullPointerException e){
            new LoadingSurveyException(e);
        }
    }
}
