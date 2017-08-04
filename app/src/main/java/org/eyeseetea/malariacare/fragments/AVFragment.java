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
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AVFragment extends ListFragment implements IDashboardFragment {


    public static final String TAG = ".SentFragment";
    protected AssessmentAdapter adapter;
    private List<SurveyDB> mSurveyDBs;
    private ListView mListView;
    AVFragment mAVFragment;

    public AVFragment() {
        mAVFragment = this;
        this.mSurveyDBs = new ArrayList();
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
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        registerFragmentReceiver();
        super.onResume();
    }

    /**
     * Inits adapter.
     * Most of times is just an AssessmentAdapter.
     * In a version with several adapters in dashboard (like in 'mock' branch) a new one like the
     * one in session is created.
     */
    private void initAdapter() {
        this.adapter = new AssessmentAdapter(getString(R.string.sent_data),
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
        Log.d(TAG, "onStop");
        unregisterFragmentReceiver();

        super.onStop();
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView() {
        if (Session.isNotFullOfUnsent(getActivity())) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View footer = inflater.inflate(this.adapter.getFooterLayout(), null, false);

            mListView = getListView();
            View button = footer.findViewById(R.id.plusButton);
            if (button != null) {
                button.setVisibility(View.GONE);
            }
            mListView.addFooterView(footer);
            LayoutUtils.setRowDivider(mListView);
            setListAdapter((BaseAdapter) adapter);
            setListShown(false);
        }
    }

    public void reloadSurveys(List<SurveyDB> newListSurveyDBs) {
        Log.d(TAG, "reloadSurveys (Thread: " + Thread.currentThread().getId() + "): "
                + newListSurveyDBs.size());
        this.mSurveyDBs.clear();
        this.mSurveyDBs.addAll(newListSurveyDBs);
        this.adapter.notifyDataSetChanged();
        setListShown(true);
    }

    public void reloadHeader(Activity activity) {
    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }

    public void reloadData() {
        //Reload data using service
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.ALL_SENT_SURVEYS_ACTION);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }
}