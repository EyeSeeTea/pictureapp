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

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.PullController;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.SyncProgressStatus;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.strategies.ProgressActivityStrategy;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

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
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        PULL_CANCEL = false;
        PULL_IS_ACTIVE = true;
        prepareUI();
    }

    private void cancellPull() {
        if (PULL_IS_ACTIVE) {
            PULL_CANCEL = true;
            PULL_IS_ACTIVE = false;
            step(getBaseContext().getResources().getString(R.string.cancellingPull));
            if (PullController.getInstance().finishPullJob()) {
                Log.d(TAG, "Logging out from sdk...");
                DhisService.logOutUser(ProgressActivity.this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Dhis2Application.bus.register(this);
        } catch (Exception e) {
            e.printStackTrace();
            Dhis2Application.bus.unregister(this);
            Dhis2Application.bus.register(this);
        }
        launchPull();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBus();
        //TODO this is not expected in pictureapp
        if (PULL_CANCEL == true) {
            finishAndGo(LoginActivity.class);
        }
    }

    private void unregisterBus() {
        try {
            Dhis2Application.bus.unregister(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void prepareUI() {
        progressBar = (ProgressBar) findViewById(R.id.pull_progress);
        progressBar.setMax(MAX_PULL_STEPS);
        textView = (TextView) findViewById(R.id.pull_text);
        final Button button = (Button) findViewById(R.id.cancelPullButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancellPull();
            }
        });
    }

    @Subscribe
    public void onProgressChange(final SyncProgressStatus syncProgressStatus) {
        if (syncProgressStatus == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (syncProgressStatus.hasError()) {
                    showException(syncProgressStatus.getException().getMessage());
                    return;
                }

                //Step
                if (syncProgressStatus.hasProgress()) {
                    step(syncProgressStatus.getMessage());
                    return;
                }

                //Finish
                if (syncProgressStatus.isFinish()) {
                    showAndMoveOn();
                }
            }
        });
    }

    /**
     * Shows a dialog with the given message y move to login after showing error
     */
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
                        DhisService.logOutUser(ProgressActivity.this);
                    }
                })
                .create()
                .show();
    }

    /**
     * Prints the step in the progress bar
     */
    private void step(final String msg) {
        final int currentProgress = progressBar.getProgress();
        progressBar.setProgress(currentProgress + 1);
        textView.setText(msg);
    }

    /**
     * Shows a dialog to tell that pull is done and then moves into the dashboard.
     */
    private void showAndMoveOn() {
        //If is not active, we need restart the process
        if (!PULL_IS_ACTIVE) {
            try {
                Dhis2Application.bus.unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishAndGo(LoginActivity.class);
            return;
        }

        //Show final step -> done
        step(getString(R.string.progress_pull_done));

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
        int stringId = R.string.dialog_title_pull_response;
        return getString(stringId);
    }

    private void launchPull() {
        progressBar.setProgress(0);
        progressBar.setMax(MAX_PULL_STEPS);
        PullController.getInstance().pull(this);
    }

    @Subscribe
    public void onLogoutFinished(UiEvent uiEvent) {
        //No event or not a logout event -> done
        if (uiEvent == null || !uiEvent.getEventType().equals(UiEvent.UiEventType.USER_LOG_OUT)) {
            return;
        }
        Log.d(TAG, "Logging out from sdk...OK");
        LogoutUseCase logoutUseCase = new LogoutUseCase(this);
        logoutUseCase.execute();
        //Go to login
        finishAndGo(LoginActivity.class);
    }

    @Override
    public void onBackPressed() {
        cancellPull();
    }

    /**
     * Finish current activity and launches an activity with the given class
     *
     * @param targetActivityClass Given target activity class
     */
    public void finishAndGo(Class targetActivityClass) {
        Intent targetActivityIntent = new Intent(this, targetActivityClass);
        finish();
        startActivity(targetActivityIntent);
    }
}
