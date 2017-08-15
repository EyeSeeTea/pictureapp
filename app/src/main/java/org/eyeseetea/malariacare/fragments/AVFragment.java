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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AVAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.util.List;

public class AVFragment extends Fragment {

    public static final String TAG = ".AVFragment";
    protected AVAdapter recyclerAdapter;
    private List<Media> mMedias;
    private RecyclerView recyclerViewList;
    private AVAdapter.ViewType activeViewType = AVAdapter.ViewType.GRID;

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

        initRecyclerView();
        initChangeModeButton();
        loadMediaListAndAdapter();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        recyclerViewList = (RecyclerView) getView().findViewById(R.id.av_recycler);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(linearLayoutManager);
    }


    private void initChangeModeButton() {
        Button changeModeButton = (Button) getView().findViewById(R.id.change_mode_button);
        changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });
    }

    private void loadMediaListAndAdapter() {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        //MediaRepository mediaRepository = new MediaRepository();

        //TODO: remove fake media repository
        MediaRepository mediaRepository = new MediaRepository();
        GetMediaUseCase getMediaUseCase = new GetMediaUseCase(mainExecutor, asyncExecutor,
                mediaRepository);
        getMediaUseCase.execute(new GetMediaUseCase.Callback() {
            @Override
            public void onSuccess(List<Media> medias) {
                mMedias = medias;
                initAdapters();
            }

            @Override
            public void onError() {
                Log.e(TAG, "error getting user program");
            }
        });
    }

    private void initAdapters() {
        this.recyclerAdapter = new AVAdapter(this.mMedias, activeViewType,
                getActivity().getApplicationContext());
        recyclerViewList.setAdapter(recyclerAdapter);
    }

    private void changeMode() {
        if (activeViewType == AVAdapter.ViewType.GRID) {
            activeViewType = AVAdapter.ViewType.LIST;
        } else {
            activeViewType = AVAdapter.ViewType.GRID;
        }
        initAdapters();
    }
}