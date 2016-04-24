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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.services.SurveyService;

import java.io.IOException;


public class DashboardActivity extends BaseActivity {

    private final static String TAG=".DashboardActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        createActionBar();

        setContentView(R.layout.fragment_dashboard);
        if (savedInstanceState == null) {
            DashboardUnsentFragment detailsFragment = new DashboardUnsentFragment();
            detailsFragment.setArguments(getIntent().getExtras());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.dashboard_details_container, detailsFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            DashboardSentFragment completedFragment = new DashboardSentFragment();
            detailsFragment.setArguments(getIntent().getExtras());
            FragmentTransaction ftr = getFragmentManager().beginTransaction();
            ftr.add(R.id.dashboard_completed_container, completedFragment);
            ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ftr.commit();
        }
        Survey.removeInProgress();
    }

    @Override
    protected void initTransition(){
        this.overridePendingTransition(R.transition.anim_slide_in_right, R.transition.anim_slide_out_right);
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        AsyncPopulateDB asyncPopulateDB=new AsyncPopulateDB();
        asyncPopulateDB.execute((Void) null);
        Survey.removeInProgress();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        Survey.removeInProgress();
    }


    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();

    }

    public void getSurveysFromService(){
        Log.d(TAG, "getSurveysFromService");
        Intent surveysIntent=new Intent(this, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        this.startService(surveysIntent);
    }

    /**
     * Just to avoid trying to navigate back from the dashboard. There's no parent activity here
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "back pressed");
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit the app?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).create().show();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AsyncPopulateDB extends AsyncTask<Void, Void, Exception> {

        private static final String DUMMY_USER="user";
        User user;

        AsyncPopulateDB() {
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                initUser();
                initDataIfRequired();
            }catch(Exception ex) {
                Log.e(TAG, "Error initializing DB: ", ex);
                return ex;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Exception exception) {
            //Error
            if(exception!=null){
                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle(R.string.dialog_title_error)
                        .setMessage(exception.getMessage())
                        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        }).create().show();
                return;
            }
            //Success
            Session.setUser(user);
            getSurveysFromService();
        }

        /**
         * Add user to table and session
         */
        private void initUser(){
            user=new User(DUMMY_USER,DUMMY_USER);
            User userdb=User.existUser(user);
            if(userdb!=null)
            user=userdb;
            else
            user.save();
        }

        private void initDataIfRequired() throws IOException {
            if (new Select().count().from(Tab.class).count()!=0) {
                Log.i(TAG, "DB Already loaded, showing surveys...");
                return;
            }

            Log.i(TAG, "DB empty, loading data ...");
            //PopulateDB.populateDummyData();
            try {
                PopulateDB.populateDB(getAssets());
            } catch (IOException e) {
                throw e;
            }
            Log.i(TAG, "DB empty, loading data ...DONE");
        }
    }

}
