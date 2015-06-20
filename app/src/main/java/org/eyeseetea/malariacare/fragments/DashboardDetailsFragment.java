/*
 * Copyright (c) 2015.
 *
 * This file is part of Facility QA Tool App.
 *
 *  Facility QA Tool App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Facility QA Tool App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.DashboardDetailsActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SurveyActivity;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeDismissListViewTouchListener;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardDetailsFragment extends ListFragment {

    private SurveyReceiver surveyReceiver;
    private List<Survey> surveys;
    protected IDashboardAdapter adapter;
    private static int index = 0;

    public DashboardDetailsFragment(){
        this.adapter = Session.getAdapter();
        this.surveys=new ArrayList<Survey>();
    }

    public static DashboardDetailsFragment newInstance(int index) {
        DashboardDetailsFragment f = new DashboardDetailsFragment();

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
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.d(".DetailsFragment", "onCreate");
        registerSurveysReceiver();
        getSurveysFromService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d(".DetailsFragment", "onCreateView");
        if (container == null) {
            return null;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(".DetailsFragment", "onActivityCreated");
        IDashboardAdapter adapterE = Session.getAdapter().newInstance(this.surveys, getActivity());
        this.adapter = adapterE;
        initListView();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Log.d(".DetailsFragment", "onListItemClick");
        super.onListItemClick(l, v, position, id);

        //Discard clicks on header|footer (which is attendend on newSurvey via super)
        if(!isPositionASurvey(position)){
            return;
        }

        //Put selected survey in session
        Session.setSurvey(surveys.get(position - 1));
        //Go to SurveyActivity
        ((DashboardDetailsActivity) getActivity()).go(SurveyActivity.class);
    }

    @Override
    public void onStop(){
        Log.d(".DetailsFragment", "onStop");
        unregisterSurveysReceiver();

        super.onStop();
    }

    /**
     * Checks if the given position points to a real survey instead of a footer or header of the listview.
     * @param position
     * @return true|false
     */
    private boolean isPositionASurvey(int position){
        return !isPositionFooter(position) && !isPositionHeader(position);
    }

    /**
     * Checks if the given position is the header of the listview instead of a real survey
     * @param position
     * @return true|false
     */
    private boolean isPositionHeader(int position){
        return position<=0;
    }

    /**
     * Checks if the given position is the footer of the listview instead of a real survey
     * @param position
     * @return true|false
     */
    private boolean isPositionFooter(int position){
        return position==(this.surveys.size()+1);
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View header = inflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View footer = inflater.inflate(this.adapter.getFooterLayout(), null, false);
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
                                return position>0 && position<=surveys.size();
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getActivity().getString(R.string.dialog_title_delete_survey))
                                            .setMessage(getActivity().getString(R.string.dialog_info_delete_survey))
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    ((Survey)adapter.getItem(position-1)).delete();
                                                    adapter.remove(adapter.getItem(position - 1));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null).create().show();
                                }

                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                new AlertDialog.Builder(getActivity())
                        .setTitle("Pushing data")
                        .setMessage("Are you sure? You can not undo this action")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                Survey survey = (Survey) adapter.getItem(position-1);

                                Log.d(".DetailsFragment", "Pushing " + survey.getProgram().getName());

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).create().show();


                return true;
            }
        });


        setListShown(false);
    }


    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    private void registerSurveysReceiver() {
        Log.d(".DetailsFragment", "registerSurveysReceiver");

        if(surveyReceiver==null){
            surveyReceiver=new SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver, new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
        }
    }


    /**
     * Unregisters the survey receiver.
     * It really important to do this, otherwise each receiver will invoke its code.
     */
    private void  unregisterSurveysReceiver(){
        if(surveyReceiver!=null){
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(surveyReceiver);
            surveyReceiver=null;
        }
    }

    /**
     * Asks SurveyService for the current list of surveys
     */
    private void getSurveysFromService(){
        Activity activity=getActivity();
        Intent surveysIntent=new Intent(activity, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD,SurveyService.ALL_UNSENT_SURVEYS_ACTION);
        activity.startService(surveysIntent);
    }

    public void reloadSurveys(List<Survey> newListSurveys){
        Log.d(".DetailsFragment", "reloadSurveys(" + newListSurveys.size() + ")");
        this.surveys.clear();
        this.surveys.addAll(newListSurveys);
        this.adapter.notifyDataSetChanged();
        setListShown(true);
    }
    /**
     * Inner private class that receives the result from the service
     */
    private class SurveyReceiver extends BroadcastReceiver{
        private SurveyReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            List<Survey> surveysFromService=(List<Survey>)Session.popServiceValue(SurveyService.ALL_UNSENT_SURVEYS_ACTION);
            reloadSurveys(surveysFromService);
        }

    }
}