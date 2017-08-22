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

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AVAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.MediaPresenter;

import java.util.ArrayList;
import java.util.List;

public class AVFragment extends Fragment implements MediaPresenter.View {

    public static final String TAG = ".AVFragment";
    protected AVAdapter mAdapter;
    private RecyclerView recyclerView;
    private MediaPresenter mPresenter;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.av_fragment, container, false);

        initializeRecyclerView();
        initializeChangeModeButton();
        initializePresenter();

        return rootView;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }

    private void initializePresenter() {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        MediaRepository mediaRepository = new MediaRepository();

        GetMediaUseCase getMediaUseCase = new GetMediaUseCase(mainExecutor, asyncExecutor,
                mediaRepository);

        mPresenter = new MediaPresenter(getMediaUseCase);
        mPresenter.attachView(this);
    }

    private void initializeRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.av_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initializeChangeModeButton() {
        Button changeModeButton = (Button) rootView.findViewById(R.id.change_mode_button);
        changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickChangeMode();
            }
        });
    }

    @Override
    public void showMediaGridMode(List<Media> mediaList) {
        refreshList(mediaList, AVAdapter.ViewType.GRID);
    }

    @Override
    public void showMediaListMode(List<Media> mediaList) {
        refreshList(mediaList, AVAdapter.ViewType.LIST);
    }

    private void refreshList(List<Media> mediaList, AVAdapter.ViewType viewType) {
        this.mAdapter = new AVAdapter(mediaList, viewType, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public void reloadData() {
        initializePresenter();
    }
}