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

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.PullController;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

/**
 * Login Screen.
 * It shows only when the user has an open session.
 */
public class LoginActivity extends org.hisp.dhis.android.sdk.ui.activities.LoginActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = ".LoginActivity";
    /**
     * DHIS server URL
     */
    private String serverUrl;

    /**
     * DHIS username account
     */
    private String username;

    /**
     * DHIS password (required since push is done natively instead of using sdk)
     */
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Populate server with the current value
        EditText serverText = (EditText) findViewById(org.hisp.dhis.android.sdk.R.id.server_url);
        serverText.setText(ServerAPIController.getServerUrl());
        //Readonly
        serverText.setEnabled(false);

        //Username, Password blanks to force real login
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setText("");
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setText("");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Subscribe
    public void onLoginFinished(NetworkJob.NetworkJobResult<ResourceType> result) {
        if(result!=null && result.getResourceType().equals(ResourceType.USERS)) {
            if(result.getResponseHolder().getApiException() == null) {
                goSettingsWithRightExtras();
            } else {
                onLoginFail(result.getResponseHolder().getApiException());
            }
        }
    }

    private void goSettingsWithRightExtras(){

        Intent intent = new Intent(LoginActivity.this,SettingsActivity.class);
        intent = propagateExtra(intent);

        finish();
        startActivity(intent);
    }

    private Intent propagateExtra(Intent intent){
        if(getIntent().getBooleanExtra(SettingsActivity.SETTINGS_CHANGING_ORGUNIT,false)){
            Log.i(TAG, "propagateExtra -> Changing orgunit");
            intent.putExtra(SettingsActivity.SETTINGS_CHANGING_ORGUNIT,true);
        }

        if(getIntent().getBooleanExtra(SettingsActivity.SETTINGS_CHANGING_SERVER,false)){
            Log.i(TAG, "propagateExtra -> Changing server");
            intent.putExtra(SettingsActivity.SETTINGS_CHANGING_SERVER,true);
        }

        intent.putExtra(SettingsActivity.LOGIN_BEFORE_CHANGE_DONE,true);
        return intent;
    }

}



