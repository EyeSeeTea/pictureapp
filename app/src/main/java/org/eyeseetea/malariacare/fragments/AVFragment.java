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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AVAdapter;
import org.eyeseetea.malariacare.services.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AVFragment extends Fragment implements IDashboardFragment {


    public static final String TAG = ".SentFragment";
    protected AVAdapter reciclerAdapter;
    private List<Media> mMedias;
    private RecyclerView recyclerViewList;
    private AVFragment.SurveyReceiver surveyReceiver;
    private AVAdapter.ViewType activeViewType = AVAdapter.ViewType.cardview;
    AVFragment mAVFragment;

    public AVFragment() {
        mAVFragment = this;
        this.mMedias = new ArrayList();
        this.mMedias.add(new Media("newfile","path", 1, "30mb")); this.mMedias.add(new Media("newfile2","path", 1, "530mb")); this.mMedias.add(new Media("newfile3","path", 1, "320mb")); this.mMedias.add(new Media("newfile4","path", 1, "310mb")); this.mMedias.add(new Media("newfile5","path", 1, "330mb")); this.mMedias.add(new Media("newfile6","path", 1, "340mb"));
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
        return inflater.inflate(R.layout.av_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initDetailedRecyclerViewList();
        initAdapters();
    }

    private void initDetailedRecyclerViewList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAVFragment.getActivity().getApplicationContext());
        recyclerViewList = (RecyclerView)  getView().findViewById(R.id.av_recycler);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(linearLayoutManager);
}

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        registerFragmentReceiver();
        super.onResume();
    }

    /**
     * Inits adapter.
     */
    private void initAdapters() {
        this.reciclerAdapter = new AVAdapter(this.mMedias, activeViewType, mAVFragment.getActivity().getApplicationContext());
        recyclerViewList.setAdapter(reciclerAdapter);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        unregisterFragmentReceiver();

        super.onStop();
    }

    public void reloadMedia(List<MediaDB> newListMediaDBs) {
        Log.d(TAG, "reloadMedia (Thread: " + Thread.currentThread().getId() + "): "
                + newListMediaDBs.size());
        this.mMedias.clear();
        this.mMedias.addAll(MediaRepository.fromModel(newListMediaDBs));
        this.reciclerAdapter.notifyDataSetChanged();
    }

    public void reloadHeader(Activity activity) {
    }

    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    public void registerFragmentReceiver() {
        Log.d(TAG, "registerSurveysReceiver");

        if (surveyReceiver == null) {
            surveyReceiver = new AVFragment.SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver,
                    new IntentFilter(SurveyService.ALL_MEDIA_ACTION));
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

    public void reloadData() {
        //Reload data using service
        Intent mediaIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        mediaIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.ALL_MEDIA_ACTION);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                mediaIntent);
    }

    public void toggleLists() {
        if(activeViewType== AVAdapter.ViewType.cardview){
            activeViewType=AVAdapter.ViewType.detailed;
        }else{
            activeViewType=AVAdapter.ViewType.cardview;
        }
        initAdapters();
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
            if (SurveyService.ALL_MEDIA_ACTION.equals(intent.getAction())) {
                List<MediaDB> mediaFromService;
                Session.valuesLock.readLock().lock();
                try {
                    mediaFromService = (List<MediaDB>) Session.popServiceValue(
                            SurveyService.ALL_MEDIA_ACTION);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadMedia(mediaFromService);
            }
        }
    }
}