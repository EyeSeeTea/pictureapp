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
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.strategies.ProgressActivityStrategy;

public class ProgressActivity extends Activity {

    private static final String TAG = ".ProgressActivity";

    /**
     * Num of expected steps while pulling
     */
    private static final int MAX_PULL_STEPS = 7;
    /**
     * Used for control new steps
     */
    public static Boolean PULL_IS_ACTIVE = false;
    /**
     * Used for control autopull from login
     */
    public static Boolean PULL_CANCEL = false;

    public ProgressActivityStrategy progressVariantAdapter = new ProgressActivityStrategy(this);
    private ProgressBar progressBar;
    private TextView textView;

    private LogoutUseCase mLogoutUseCase;
    private PullUseCase mPullUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeDependencies();

        setContentView(R.layout.activity_progress);

        prepareUI();
    }

    private void initializeDependencies() {
        AuthenticationManager authenticationManager = new AuthenticationManager(this);

        IPullController pullController = new PullController(this);

        mLogoutUseCase = new LogoutUseCase(authenticationManager);
        mPullUseCase = new PullUseCase(pullController);
    }

    private void prepareUI() {
        PULL_CANCEL = false;
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
            PULL_CANCEL = true;
            PULL_IS_ACTIVE = false;
            showProgressText(PreferencesState.getInstance().getContext().getResources().getString(
                    R.string.cancellingPull));

            //TODO jsanchez
/*            if (PullController.getInstance().finishPullJob()) {
                Log.d(TAG, "Logging out from sdk...");
                executeLogout();
            }*/
        }
    }

    private void executeLogout() {
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

    @Override
    public void onPause() {
        super.onPause();
        //TODO this is not expected in pictureapp
        if (PULL_CANCEL == true) {
            finishAndGo(LoginActivity.class);
        }
    }

    private void launchPull(boolean isDemo) {


        mPullUseCase.execute(isDemo, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                showAndMoveOn();
            }

            @Override
            public void onStep(PullStep pullStep) {
                switch (pullStep) {
                    case METADATA:
                        showProgressText(PreferencesState.getInstance().getContext().getString(
                                R.string.progress_pull_downloading));
                        break;
                }

            }

            @Override
            public void onError(String message) {
                showException(PreferencesState.getInstance().getContext().getString(R.string
                        .dialog_pull_error));
            }

            @Override
            public void onNetworkError() {
                showException(PreferencesState.getInstance().getContext().getString(
                        R.string.network_error));
            }
        });

    }

    private void showException(String msg) {
        String title = getDialogTitle();

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(msg)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //A crash during a pull requires to start from scratch -> logout
                        Log.d(TAG, "Logging out from sdk...");
                        executeLogout();
                    }
                })
                .create()
                .show();
    }

    private void showProgressText(final String msg) {
        final int currentProgress = progressBar.getProgress();
        progressBar.setProgress(currentProgress + 1);
        textView.setText(msg);
    }

    private void showAndMoveOn() {
        //If is not active, we need restart the process
        if (!PULL_IS_ACTIVE) {
            finishAndGo(LoginActivity.class);
            return;
        }

        showProgressText(getString(R.string.progress_pull_done));

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
