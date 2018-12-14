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
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeDismissListViewTouchListener;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.strategies.DashboardUnsentFragmentStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardUnsentFragment extends ListFragment implements IDashboardFragment {


    public static final String TAG = ".UnsentFragment";
    protected AssessmentAdapter adapter;
    private SurveyReceiver surveyReceiver;
    private List<SurveyDB> mSurveyDBs;
    private DashboardUnsentFragment dashboardUnsentFragment;
    private boolean viewCreated = false;
    ListView listView;

    public DashboardUnsentFragment() {
        this.mSurveyDBs = new ArrayList();
        dashboardUnsentFragment = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }
        viewCreated = true;

        return inflater.inflate(R.layout.unsent_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        initListView();
        registerForContextMenu(listView);
    }


    @Override
    public void onResume() {
        Log.d(TAG, "AndroidLifeCycle: onResume");
        registerFragmentReceiver();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AndroidLifeCycle: onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "AndroidLifeCycle: onStart");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "AndroidLifeCycle: onPause");
        super.onPause();
    }

    /**
     * Inits adapter.
     * Most of times is just an AssessmentAdapter.
     * In a version with several adapters in dashboard (like in 'mock' branch) a new one like the
     * one in session is created.
     */
    private void initAdapter() {
        this.adapter = new AssessmentAdapter(getString(R.string.unsent_data),
                this.mSurveyDBs, getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        super.onListItemClick(l, v, position, id);
        adapter.onClick(l, position, mSurveyDBs);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "AndroidLifeCycle: onStop");
        unregisterFragmentReceiver();

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View header = DashboardHeaderStrategy.getInstance().loadHeader(this.adapter.getHeaderLayout(),
                inflater);
        final View footer = inflater.inflate(this.adapter.getFooterLayout(), null, false);

        listView = getListView();
        if (header != null) {
            listView.addHeaderView(header);
        }
        listView.addFooterView(footer);
        LayoutUtils.setRowDivider(listView);
        setListAdapter((BaseAdapter) adapter);

        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return position > 0 && position <= mSurveyDBs.size();
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getActivity().getString(
                                                    R.string.dialog_title_delete_survey))
                                            .setMessage(getActivity().getString(
                                                    R.string.dialog_info_delete_survey))
                                            .setPositiveButton(android.R.string.yes,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0,
                                                                int arg1) {
                                                            DashboardUnsentFragmentStrategy
                                                                    .deleteSurvey(
                                                                    ((SurveyDB) adapter.getItem(
                                                                            position - 1)));

                                                            //Reload data using service
                                                            Intent surveysIntent = new Intent(
                                                                    getActivity(),
                                                                    SurveyService.class);
                                                            surveysIntent.putExtra(
                                                                    SurveyService.SERVICE_METHOD,
                                                                    SurveyService
                                                                            .RELOAD_DASHBOARD_ACTION);
                                                            getActivity().startService(
                                                                    surveysIntent);
                                                        }
                                                    })
                                            .setNegativeButton(android.R.string.no,
                                                    null).create().show();
                                }

                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }


    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    public void registerFragmentReceiver() {
        Log.d(TAG, "initializeSurvey");

        if (surveyReceiver == null) {
            surveyReceiver = new SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver,
                    new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
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

    public void reloadHeader(Activity activity) {
        dashboardUnsentFragment.reloadHeader(activity, R.string.tab_tag_assess);
    }

    public void reloadHeader(Activity activity, int id) {
        DashboardHeaderStrategy.getInstance().init(activity, id);
    }


    public void reloadData() {
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }

    public void reloadSurveys(List<SurveyDB> newListSurveyDBs) {
        Log.d(TAG, "reloadSurveys (Thread: " + Thread.currentThread().getId() + "): "
                + newListSurveyDBs.size());
        this.mSurveyDBs.clear();
        this.mSurveyDBs.addAll(newListSurveyDBs);

        this.adapter.notifyDataSetChanged();
        if (viewCreated) {
            LayoutUtils.measureListViewHeightBasedOnChildren(listView);
        }
    }

    public void reloadSurveysFromService(List<SurveyDB> surveysUnsentFromService) {
        reloadSurveys(surveysUnsentFromService);
        LayoutUtils.setRowDivider(listView);
        // Measure the screen height
        int screenHeight = LayoutUtils.measureScreenHeight(getActivity());

        // Get the unsent list height, measured when reloading the surveys
        int unsentHeight = LayoutUtils.getUnsentListHeight();

        // Set the variable that establish the unsent list is shown or not
        if (unsentHeight >= screenHeight) {
            Session.setFullOfUnsent(getActivity());
        } else {
            Session.setNotFullOfUnsent(getActivity());
        }
    }

    /**
     * Inner private class that receives the result from the service
     */
    public class SurveyReceiver extends BroadcastReceiver {
        private SurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //Listening only intents from this method
            if (SurveyService.ALL_UNSENT_SURVEYS_ACTION.equals(intent.getAction())) {
                List<SurveyDB> surveysUnsentFromService;
                Session.valuesLock.readLock().lock();
                try {
                    surveysUnsentFromService = (List<SurveyDB>) Session.popServiceValue(
                            SurveyService.ALL_UNSENT_SURVEYS_ACTION);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadSurveysFromService(surveysUnsentFromService);
            }
        }
    }
}