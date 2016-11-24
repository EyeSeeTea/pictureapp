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

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentUnsentAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeDismissListViewTouchListener;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.PushResult;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardUnsentFragment extends ListFragment {


    public static final String TAG = ".UnsentFragment";
    protected IDashboardAdapter adapter;
    private SurveyReceiver surveyReceiver;
    private List<Survey> surveys;

    public DashboardUnsentFragment() {
        this.adapter = Session.getAdapterUncompleted();
        this.surveys = new ArrayList();
    }

    public static DashboardUnsentFragment newInstance(int index) {
        DashboardUnsentFragment f = new DashboardUnsentFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        initListView();
        registerForContextMenu(getListView());
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        registerSurveysReceiver();

        super.onResume();
    }

    /**
     * Inits adapter.
     * Most of times is just an AssessmentAdapter.
     * In a version with several adapters in dashboard (like in 'mock' branch) a new one like the
     * one in session is created.
     */
    private void initAdapter() {
        IDashboardAdapter adapterInSession = Session.getAdapterUncompleted();
        if (adapterInSession == null) {
            adapterInSession = new AssessmentUnsentAdapter(this.surveys, getActivity());
        } else {
            adapterInSession = adapterInSession.newInstance(this.surveys, getActivity());
        }
        this.adapter = adapterInSession;
        Session.setAdapterUncompleted(this.adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");
        super.onListItemClick(l, v, position, id);

        //Discard clicks on header|footer (which is attendend on newSurvey via super)
        if (!isPositionASurvey(position)) {
            return;
        }

        //Put selected survey in session
        Session.setSurvey(surveys.get(position - 1));
        //Go to SurveyActivity
        DashboardActivity.dashboardActivity.openSentSurvey();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        unregisterSurveysReceiver();

        super.onStop();
    }

    /**
     * Checks if the given position points to a real survey instead of a footer or header of the
     * listview.
     *
     * @return true|false
     */
    private boolean isPositionASurvey(int position) {
        return !isPositionFooter(position) && !isPositionHeader(position);
    }

    /**
     * Checks if the given position is the header of the listview instead of a real survey
     *
     * @return true|false
     */
    private boolean isPositionHeader(int position) {
        return position <= 0;
    }

    /**
     * Checks if the given position is the footer of the listview instead of a real survey
     *
     * @return true|false
     */
    private boolean isPositionFooter(int position) {
        return position == (this.surveys.size() + 1);
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View header = inflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View footer = inflater.inflate(this.adapter.getFooterLayout(), null, false);
        //TextCard title = (TextCard) getActivity().findViewById(R.id.titleInProgress);
        //title.setText(adapter.getTitle());
        ListView listView = getListView();
        listView.addHeaderView(header);
        listView.addFooterView(footer);
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
                                return position > 0 && position <= surveys.size();
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
                                                            ((Survey) adapter.getItem(
                                                                    position - 1)).delete();
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

        // Remove long click as push is done automatically now
//        listView.setLongClickable(true);
//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
// position, long id) {
//
//
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(R.string.dialog_title_push)
//                        .setMessage(R.string.dialog_info_push_confirm)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface
// .OnClickListener() {
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                final Survey survey = (Survey) adapter.getItem(position-1);
//                                AsyncPush asyncPush=new AsyncPush(survey);
//                                asyncPush.execute((Void) null);
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null).create().show();
//
//
//                return true;
//            }
//        });


        setListShown(false);
    }


    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    private void registerSurveysReceiver() {
        Log.d(TAG, "registerSurveysReceiver");

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
    public void unregisterSurveysReceiver() {
        Log.d(TAG, "unregisterSurveysReceiver");
        if (surveyReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(surveyReceiver);
            surveyReceiver = null;
        }
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

    public void reloadSurveys(List<Survey> newListSurveys) {
        Log.d(TAG, "reloadSurveys (Thread: " + Thread.currentThread().getId() + "): "
                + newListSurveys.size());
        this.surveys.clear();
        this.surveys.addAll(newListSurveys);
        this.adapter.notifyDataSetChanged();
        LayoutUtils.measureListViewHeightBasedOnChildren(getListView());
        setListShown(true);
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
            //Listening only intents from this method
            if (SurveyService.ALL_UNSENT_SURVEYS_ACTION.equals(intent.getAction())) {
                List<Survey> surveysUnsentFromService;
                Session.valuesLock.readLock().lock();
                try {
                    surveysUnsentFromService = (List<Survey>) Session.popServiceValue(
                            SurveyService.ALL_UNSENT_SURVEYS_ACTION);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadSurveys(surveysUnsentFromService);
                LayoutUtils.setDivider(getListView());
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
        }
    }

    public class AsyncPush extends AsyncTask<Void, Integer, PushResult> {

        private Survey survey;


        public AsyncPush(Survey survey) {
            this.survey = survey;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //spinner
            setListShown(false);
        }

        @Override
        protected PushResult doInBackground(Void... params) {
            PushClient pushClient = new PushClient(survey, getActivity());
            return pushClient.push();
        }

        @Override
        protected void onPostExecute(PushResult pushResult) {
            super.onPostExecute(pushResult);
            setListShown(true);
            showResponse(pushResult);
        }

        /**
         * Shows the proper response message
         */
        private void showResponse(PushResult pushResult) {
            String msg;
            if (pushResult.isSuccessful()) {
                msg = getActivity().getResources().getString(R.string.dialog_info_push_ok) + " \n"
                        + String.format("Imported: %s | Updated: %s | Ignored: %s",
                        pushResult.getImported(), pushResult.getUpdated(), pushResult.getIgnored());
            } else if (pushResult.getImported().equals("0")) {
                msg = getActivity().getResources().getString(
                        R.string.dialog_info_push_bad_credentials);
            } else {
                msg = pushResult.getException().getMessage();
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getString(R.string.dialog_title_push_response))
                    .setMessage(msg)
                    .setNeutralButton(android.R.string.yes, null).create().show();

        }
    }


}