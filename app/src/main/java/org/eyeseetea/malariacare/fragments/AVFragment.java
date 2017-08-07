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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AVAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AVFragment extends Fragment implements IDashboardFragment {


    public static final String TAG = ".SentFragment";
    protected AVAdapter recyclerAdapter;
    private List<Media> mMedias;
    private RecyclerView recyclerViewList;
    private AVAdapter.ViewType activeViewType = AVAdapter.ViewType.cardview;
    AVFragment mAVFragment;

    public AVFragment() {
        mAVFragment = this;
        this.mMedias = new ArrayList();
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

        loadMediaListAndAdapter();
    }

    private void loadMediaListAndAdapter() {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();

        GetMediaUseCase getMediaUseCase = new GetMediaUseCase(mainExecutor, asyncExecutor);
        getMediaUseCase.execute(new GetMediaUseCase.Callback() {
            @Override
            public void onSuccess(List<Media> medias) {
                mMedias=medias;
                initAdapters();
            }

            @Override
            public void onError() {
                Log.e(TAG, "error getting user program");
            }
        });
    }

    private void initDetailedRecyclerViewList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAVFragment.getActivity().getApplicationContext());
        recyclerViewList = (RecyclerView)  getView().findViewById(R.id.av_recycler);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(linearLayoutManager);
    }

    /**
     * Inits adapter.
     */
    private void initAdapters() {
        this.recyclerAdapter = new AVAdapter(this.mMedias, activeViewType, mAVFragment.getActivity().getApplicationContext());
        recyclerViewList.setAdapter(recyclerAdapter);
    }

    public void reloadMedia(List<MediaDB> newListMediaDBs) {
        Log.d(TAG, "reloadMedia (Thread: " + Thread.currentThread().getId() + "): "
                + newListMediaDBs.size());
        this.mMedias.clear();
        this.mMedias.addAll(MediaRepository.fromModel(newListMediaDBs));
        this.recyclerAdapter.notifyDataSetChanged();
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
        loadMediaListAndAdapter();
    }

    public void toggleLists() {
        if(activeViewType== AVAdapter.ViewType.cardview){
            activeViewType=AVAdapter.ViewType.detailed;
        }else{
            activeViewType=AVAdapter.ViewType.cardview;
        }
        initAdapters();
    }
}