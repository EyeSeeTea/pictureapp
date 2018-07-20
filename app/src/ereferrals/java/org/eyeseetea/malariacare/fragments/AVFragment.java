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

import static org.eyeseetea.malariacare.views.ViewUtils.isThereAnAppThatCanHandleThis;
import static org.eyeseetea.malariacare.views.ViewUtils.showToast;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository.MediaListMode;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AVAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.MediaPresenter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.io.File;
import java.util.List;

public class AVFragment extends Fragment implements MediaPresenter.View {

    public static final String TAG = ".AVFragment";
    protected AVAdapter mAdapter;
    private RecyclerView recyclerView;
    private MediaPresenter mPresenter;
    CustomTextView mTextProgressView;
    private CustomTextView mErrorMessage;
    IMainExecutor mainExecutor;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.av_fragment, container, false);

        mTextProgressView = (CustomTextView)rootView.findViewById(R.id.progress_text);
        mErrorMessage = (CustomTextView) rootView.findViewById(R.id.error_message);

        initializeRecyclerView();
        initializeChangeModeButtons();
        initializePresenter();
        return rootView;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }

    private void initializePresenter() {
        mainExecutor= new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMediaRepository mediaRepository = new MediaRepository();

        ISettingsRepository settingsDataSource = new SettingsDataSource(getActivity().getBaseContext());
        GetMediaUseCase getMediaUseCase = new GetMediaUseCase(mainExecutor, asyncExecutor,
                mediaRepository);
        GetSettingsUseCase getSettingsUseCase = new GetSettingsUseCase(mainExecutor, asyncExecutor,
                settingsDataSource);
        SaveSettingsUseCase saveSettingsUseCase = new SaveSettingsUseCase(mainExecutor, asyncExecutor,
                settingsDataSource);
        mPresenter = new MediaPresenter(getMediaUseCase, getSettingsUseCase, saveSettingsUseCase);
        mPresenter.attachView(this);
    }

    private void initializeRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.av_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initializeChangeModeButtons() {
        ImageButton gridMode = (ImageButton) rootView.findViewById(R.id.av_grid_mode);
        ImageButton listMode = (ImageButton) rootView.findViewById(R.id.av_list_mode);
        gridMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickChangeMode(MediaListMode.GRID);
            }
        });
        listMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClickChangeMode(MediaListMode.LIST);
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

    @Override
    public void openMedia(String resourcePath) {
        Intent implicitIntent = new Intent();
        implicitIntent.setAction(Intent.ACTION_VIEW);
        File file = new File(resourcePath);
        Uri contentUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".layout.adapters.dashboard.AVAdapter", file);

        implicitIntent.setDataAndType(contentUri,
                PreferencesState.getInstance().getContext().getContentResolver().getType(
                        contentUri));
        implicitIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        implicitIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        if (isThereAnAppThatCanHandleThis(implicitIntent, getActivity())) {
            getActivity().startActivity(Intent.createChooser(implicitIntent,
                    PreferencesState.getInstance().getContext().getString(
                            R.string.feedback_view_image)));
        } else {
            showToast(R.string.error_unable_to_find_app_than_can_open_file, getActivity());
        }
    }

    private void refreshList(List<Media> mediaList, AVAdapter.ViewType viewType) {
        this.mAdapter = new AVAdapter(mediaList, viewType, getActivity());

        mAdapter.setOnClickMediaListener(new AVAdapter.OnClickMediaListener() {
            @Override
            public void onClick(Media media) {
                mPresenter.onClickMedia(media);
            }
        });
        RecyclerView.LayoutManager listLayoutManager = null;
        if (viewType == AVAdapter.ViewType.GRID) {
            listLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            listLayoutManager = new LinearLayoutManager(getActivity());
        }
        recyclerView.setLayoutManager(listLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    public void reloadData() {
        initializePresenter();
    }

    public void showProgress(final  boolean isInProgress) {
        if(isInProgress) {
            mTextProgressView.setVisibility(android.view.View.VISIBLE);
            mErrorMessage.setVisibility(View.GONE);
        }else{
            mTextProgressView.setVisibility(android.view.View.GONE);
        }
    }

    public void showError(int message, boolean hasError) {
        if (mPresenter.canShowErrorMessage()) {
            mErrorMessage.setVisibility(hasError ? View.VISIBLE : View.GONE);
            if (hasError) {
                mErrorMessage.setText(message);
            }
        }else {
            mErrorMessage.setVisibility(View.GONE);
        }
    }


    public void hideHeader() {
        DashboardHeaderStrategy.getInstance().hideHeader(getActivity());
    }
}