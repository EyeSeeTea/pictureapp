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

package org.eyeseetea.malariacare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.strategies.ProgressActivityStrategy;

public class ProgressActivity extends Activity {

    private static final String TAG = ".ProgressActivity";

    private static final int MAX_PULL_STEPS = 5;

    public static Boolean PULL_IS_ACTIVE = false;

    public ProgressActivityStrategy progressVariantAdapter = new ProgressActivityStrategy(this);
    private ProgressBar progressBar;
    private TextView textView;

    private LogoutUseCase mLogoutUseCase;
    private PullUseCase mPullUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesState.getInstance().onCreateActivityPreferences(getResources(), getTheme());

        initializeDependencies();

        setContentView(R.layout.activity_progress);

        prepareUI();
    }

    private void initializeDependencies() {
        AuthenticationManager authenticationManager = new AuthenticationManager(this);

        IPullController pullController = new PullController(this);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();

        mLogoutUseCase = new LogoutUseCase(authenticationManager);
        mPullUseCase = new PullUseCase(pullController, asyncExecutor, mainExecutor);
    }

    private void prepareUI() {
        PULL_IS_ACTIVE = true;

        progressBar = (ProgressBar) findViewById(R.id.pull_progress);
        progressBar.setMax(MAX_PULL_STEPS);
        progressBar.setProgress(0);
        textView = (TextView) findViewById(R.id.pull_text);
        final Button button = (Button) findViewById(R.id.cancelPullButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancellPull();
            }
        });
    }

    private void cancellPull() {
        if (PULL_IS_ACTIVE) {
            PULL_IS_ACTIVE = false;
            showProgressText(R.string.cancellingPull);
            mPullUseCase.cancel();
        }
    }

    private void executeLogout() {
        AlarmPushReceiver.cancelPushAlarm(this);
        mLogoutUseCase.execute(new LogoutUseCase.Callback() {
            @Override
            public void onLogoutSuccess() {
                finishAndGo(LoginActivity.class);
            }

            @Override
            public void onLogoutError(String message) {
                Log.e(TAG, message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        launchPull(Session.getCredentials().isDemoCredentials());
    }

    private void launchPull(boolean isDemo) {
        PullFilters pullFilters = new PullFilters();
        pullFilters.setStartDate(PreferencesState.getInstance().getDateStarDateLimitFilter());
        pullFilters.setDownloadDataRequired(PreferencesState.getInstance().downloadDataFilter());
        pullFilters.setPullDataAfterMetadata(PreferencesState.getInstance().getPullDataAfterMetadata());
        pullFilters.setPullMetaData(PreferencesState.getInstance().downloadMetaData());
        if(PreferencesState.getInstance().getDataFilteredByOrgUnit()) {
            pullFilters.setDataByOrgUnit(PreferencesState.getInstance().getOrgUnit());
        }
        pullFilters.setDemo(isDemo);

        mPullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                showAndMoveOn();
            }

            @Override
            public void onStep(PullStep pullStep) {
                switch (pullStep) {
                    case METADATA:
                        showProgressText(R.string.progress_pull_downloading);
                        break;
                    case CONVERT_METADATA:
                        showProgressText(R.string.progress_pull_preparing_orgs);
                    case CONVERT_DATA:
                        showProgressText(R.string.progress_pull_surveys);
                    case BUILDING_SURVEYS:
                        showProgressText(R.string.progress_pull_building_survey);
                    case BUILDING_VALUES:
                        showProgressText(R.string.progress_pull_building_value);
                }
            }

            @Override
            public void onError(String message) {
                showException(R.string.dialog_pull_error);
            }

            @Override
            public void onNetworkError() {
                showException(R.string.network_error);
            }

            @Override
            public void onPullConversionError() {
                showException(R.string.dialog_pull_error);
            }

            @Override
            public void onCancel() {
                executeLogout();
            }
        });

    }

    private void showException(int stringId) {
        String title = getDialogTitle();
        Log.d(TAG, getString(stringId));
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(getString(stringId))
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Logging out from sdk...");
                        executeLogout();
                    }
                })
                .create()
                .show();
    }

    private void showProgressText(int stringId) {
        final int currentProgress = progressBar.getProgress();
        progressBar.setProgress(currentProgress + 1);
        textView.setText(getString(stringId));
    }

    private void showAndMoveOn() {
        showProgressText(R.string.progress_pull_done);

        String title = getDialogTitle();

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(R.string.dialog_pull_success)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        progressVariantAdapter.finishAndGo();
                    }
                }).create().show();

    }

    private String getDialogTitle() {
        return getString(R.string.dialog_title_pull_response);
    }

    @Override
    public void onBackPressed() {
        cancellPull();
    }

    public void finishAndGo(Class targetActivityClass) {
        Intent targetActivityIntent = new Intent(this, targetActivityClass);
        finish();
        startActivity(targetActivityIntent);
    }
}
