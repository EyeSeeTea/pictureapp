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
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.PullController;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.io.InputStream;

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


       if (User.getLoggedUser() != null && !ProgressActivity.PULL_CANCEL ) {
            startActivity(new Intent(LoginActivity.this,
                   ((Dhis2Application) getApplication()).getMainActivity()));

           finish();


       }
       ProgressActivity.PULL_CANCEL =false;
           //Populate server with the current value
        EditText serverText = (EditText) findViewById(org.hisp.dhis.android.sdk.R.id.server_url);
        serverText.setText(ServerAPIController.getServerUrl());
        //Readonly
        //serverText.setEnabled(false);

       SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
       EditText passwordEditText = (EditText) findViewById(R.id.password);
       EditText usernameEditText = (EditText) findViewById(R.id.username);
       if (sharedPreferences.getString(getResources().getString(R.string.dhis_user),"").isEmpty()) {
           usernameEditText.setText("");

           passwordEditText.setText("");
       } else {
           usernameEditText.setText(sharedPreferences.getString(getResources().getString(R.string.dhis_user), ""));
           passwordEditText.setText(sharedPreferences.getString(getResources().getString(R.string.dhis_password), ""));
       }

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

    @Override
    public void onClick(final View v) {



       if (!isEulaAccepted()) {


           InputStream message = getResources().openRawResource(R.raw.eula);
           String stringMessage = Utils.convertFromInputStreamToString(message).toString();
           final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
           Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

           AlertDialog dialog = new AlertDialog.Builder(this)
                   .setTitle(getString(R.string.settings_menu_eula))
                   .setMessage(linkedMessage)
                   .setCancelable(false)
                   .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           EditText serverEditText = (EditText) findViewById(R.id.server_url);
                           PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverEditText.getText().toString());
                           setEulaAccepted();
                           eulaAccepted(v);

                       }
                   })
                   .setNegativeButton(android.R.string.no, null).create();
           dialog.show();
           ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());



        } else {

           EditText serverEditText = (EditText) findViewById(R.id.server_url);
           PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, serverEditText.getText().toString());
           super.onClick(v);
        }

        // Save dhis URL and establish in preferences, so it will be used to make the pull

    }

    public void eulaAccepted(View v) {
        super.onClick(v);
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

        EditText passwordEditText = (EditText) findViewById(R.id.password);
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, usernameEditText.getText().toString());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, passwordEditText.getText().toString());


        finish();
        startActivity(new Intent(this, ProgressActivity.class));



      //  Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
      //  intent = propagateExtraAndResult(intent);

      //  finish();
       // if(!getIntent().getBooleanExtra(SettingsActivity.SETTINGS_EULA_ACCEPTED, false))
          //  startActivity(intent);
    }

    private Intent propagateExtraAndResult(Intent intent){
        if(getIntent().getBooleanExtra(SettingsActivity.SETTINGS_CHANGING_ORGUNIT,false)){
            Log.i(TAG, "propagateExtraAndResult -> Changing orgunit");
            intent.putExtra(SettingsActivity.SETTINGS_CHANGING_ORGUNIT,true);
        }

        if(getIntent().getBooleanExtra(SettingsActivity.SETTINGS_CHANGING_SERVER,false)){
            Log.i(TAG, "propagateExtraAndResult -> Changing server");
            intent.putExtra(SettingsActivity.SETTINGS_CHANGING_SERVER,true);
        }

        if(getIntent().getBooleanExtra(SettingsActivity.SETTINGS_EULA_ACCEPTED, false)){
            Log.i(TAG, "propagateExtraAndResult -> EULA accepted, Server overwrite from "+PreferencesState.getInstance().getDhisURL() +" to "+getUserIntroducedServer());
            PreferencesState.getInstance().setDhisURL(getUserIntroducedServer());
            setResult(RESULT_OK, intent);
        } else {
            if (isEulaAccepted() && !getServerFromPreferences().equals(getUserIntroducedServer())) {
                Log.i(TAG, "propagateExtraAndResult -> Server changed from "+PreferencesState.getInstance().getDhisURL() +" to "+getUserIntroducedServer());
                //If the user change the server, the getServerFromPreferents have the old server value only before to call reloadPreferences()
                PreferencesState.getInstance().reloadPreferences();
                PreferencesState.getInstance().setIsNewServerUrl(true);
            }
        }

        intent.putExtra(SettingsActivity.LOGIN_BEFORE_CHANGE_DONE,true);
        return intent;
    }

    /**
     * Check whether the EULA has already been accepted by the user. When the user accepts the EULA,
     * a preference is set so the app will remind between different executions
     * @return
     */
     private boolean isEulaAccepted(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(getApplicationContext().getResources().getString(R.string.eula_accepted), false);
    }

    private void setEulaAccepted(){
        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sharedPreferences.putBoolean(getApplicationContext().getResources().getString(R.string.eula_accepted), true).commit();
    }

    /**
     * Get from the server textfield what the user introduced
     * @return
     */
    private String getUserIntroducedServer(){
        EditText serverEditText = (EditText) findViewById(R.id.server_url);
        return serverEditText.getText().toString();
    }

    /**
     * Get from the preferences the server setting
     * @return
     */
     private String getServerFromPreferences(){
        return PreferencesState.getInstance().getDhisURL();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Every BaseActivity(Details, Create, Survey) goes back to DashBoard
     */
    public void onBackPressed(){
       finish();
        //finishAndGo(DashboardActivity.class);
    }

    /**
     * Finish current activity and launches an activity with the given class
     * @param targetActivityClass Given target activity class
     */
     public void finishAndGo(Class targetActivityClass){
        Intent targetActivityIntent = new Intent(this,targetActivityClass);
        finish();
        startActivity(targetActivityIntent);
    }





}


