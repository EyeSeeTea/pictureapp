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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.listeners.SurveyLocationListener;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.receivers.AlarmPushReceiver;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.io.InputStream;
import java.util.List;


public abstract class BaseActivity extends ActionBarActivity {

    /**
     * Extra param to annotate the activity to return after settings
     */
    public static final String SETTINGS_CALLER_ACTIVITY = "SETTINGS_CALLER_ACTIVITY";

    protected static String TAG=".BaseActivity";

    private AlarmPushReceiver alarmPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferencesState.getInstance().loadsLanguageInActivity();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);

        initView(savedInstanceState);
        if(PushController.getInstance().isPushInProgress()) {
            List<Survey> surveys=Survey.getAllSendingSurveys();
            Log.d(TAG,"The app was closed in the middle of a push. Surveys sending: "+surveys.size());
            for(Survey survey:surveys){
                survey.setStatus(Constants.SURVEY_QUARANTINE);
                survey.save();
            }
            PushController.getInstance().setPushInProgress(false);
        }
        alarmPush = new AlarmPushReceiver();
        alarmPush.setPushAlarm(this);
    }

    /**
     * Common styling
     */
    private void initView(Bundle savedInstanceState){
        setTheme(R.style.EyeSeeTheme);
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        LayoutUtils.setActionBarLogo(actionBar);

        if (savedInstanceState == null){
            initTransition();
        }
    }

    /**
     * Adds actionbar to the activity
     */
    public void createActionBar(){
        Program program = Program.getFirstProgram();

        if (program != null) {
            android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
            LayoutUtils.setActionBarLogo(actionBar);
            LayoutUtils.setActionBarText(actionBar, PreferencesState.getInstance().getOrgUnit(), this.getResources().getString(R.string.app_name));
        }
    }

    /**
     * Customize transitions for these activities
     */
    protected void initTransition() {
        this.overridePendingTransition(R.transition.anim_slide_in_left, R.transition.anim_slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                debugMessage("User asked for settings");
                if(PushController.getInstance().isPushInProgress()) {
                    Log.d(TAG,"Click in settings true "+PushController.getInstance().isPushInProgress());
                    Toast.makeText(this, R.string.toast_push_is_pushing, Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"Click in settings false "+PushController.getInstance().isPushInProgress());
                    goSettings();
                }
                break;
            case R.id.action_about:
                debugMessage("User asked for about");
                showAlertWithHtmlMessageAndLastCommit(R.string.settings_menu_about, R.raw.about, BaseActivity.this);
                break;
            case R.id.action_copyright:
                debugMessage("User asked for copyright");
                showAlertWithMessage(R.string.settings_menu_copyright, R.raw.copyright);
                break;
            case R.id.action_licenses:
                debugMessage("User asked for software licenses");
                showAlertWithHtmlMessage(R.string.settings_menu_licenses, R.raw.licenses);
                break;
            case R.id.action_eula:
                debugMessage("User asked for EULA");
                showAlertWithHtmlMessage(R.string.settings_menu_eula, R.raw.eula);
                break;
            case android.R.id.home:
                debugMessage("Go back");
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
        finishAndGo(DashboardActivity.class);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent;
        intent = (getCallingActivity() != null) ? new Intent(getCallingActivity().getClassName()) : getIntent();

        if (intent.getStringExtra("activity") != null && getCallingActivity() != null && intent.getStringExtra("activity").equals("settings")){
            Log.i(".onResume", "coming from settings");
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();

            overridePendingTransition(0, 0);
            startActivity(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void goSettings(){
        Intent intentSettings=new Intent(this,SettingsActivity.class);
        intentSettings.putExtra(SETTINGS_CALLER_ACTIVITY, this.getClass());
        startActivity(new Intent(this, SettingsActivity.class));
    }



    public void prepareLocationListener(Survey survey){

        SurveyLocationListener locationListener = new SurveyLocationListener(survey.getId_survey());
        LocationManager locationManager = (LocationManager) LocationMemory.getContext().getSystemService(Context.LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via NETWORK");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via GPS");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(lastLocation != null) {
                Log.d(TAG, "location not available via GPS|NETWORK, last know: "+lastLocation);
                locationListener.saveLocation(lastLocation);
            }else{
                String defaultLatitude  = getApplicationContext().getString(R.string.GPS_LATITUDE_DEFAULT);
                String defaultLongitude = getApplicationContext().getString(R.string.GPS_LONGITUDE_DEFAULT);
                Location defaultLocation = new Location(getApplicationContext().getString(R.string.GPS_PROVIDER_DEFAULT));
                defaultLocation.setLatitude(Double.parseDouble(defaultLatitude));
                defaultLocation.setLongitude(Double.parseDouble(defaultLongitude));
                Log.d(TAG, "location not available via GPS|NETWORK, default: "+defaultLocation);
                locationListener.saveLocation(defaultLocation);
            }
        }
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

    /**
     * Launches an activity with the given class
     * @param targetActivityClass Given target activity class
     */
    public void go(Class targetActivityClass){
        Intent targetActivityIntent = new Intent(this,targetActivityClass);
        startActivity(targetActivityIntent);
    }

    /**
     * Shows an alert dialog with a big message inside based on a raw resource
     * @param titleId Id of the title resource
     * @param rawId Id of the raw text resource
     */
    private void showAlertWithMessage(int titleId, int rawId){
        InputStream message = getApplicationContext().getResources().openRawResource(rawId);
        new AlertDialog.Builder(this)
                .setTitle(getApplicationContext().getString(titleId))
                .setMessage(Utils.convertFromInputStreamToString(message))
                .setNeutralButton(android.R.string.ok, null).create().show();
    }
    /**
     * Shows an alert dialog with a big message inside based on a raw resource HTML formatted
     * @param titleId Id of the title resource
     * @param rawId Id of the raw text resource in HTML format
     */
    private void showAlertWithHtmlMessage(int titleId, int rawId){
        InputStream message = getApplicationContext().getResources().openRawResource(rawId);
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(Utils.convertFromInputStreamToString(message).toString()));
        Linkify.addLinks(linkedMessage, Linkify.ALL);
        showAlert(titleId, linkedMessage);
    }

    /**
            * Shows an alert dialog with a big message inside based on a raw resource HTML formatted
    * @param titleId Id of the title resource
    * @param rawId Id of the raw text resource in HTML format
    */
    public void showAlertWithHtmlMessageAndLastCommit(int titleId, int rawId, Context context){
        String stringMessage = getMessageWithCommit(rawId, context);
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        showAlertWithLogoAndVersion(titleId, linkedMessage, context);
    }


    /**
     * Merge the lastcommit into the raw file
     * @param rawId Id of the raw text resource in HTML format
     */
    public String getMessageWithCommit(int rawId, Context context) {
        InputStream message = context.getResources().openRawResource(rawId);

        String stringCommit = Utils.getCommitHash(context);
        String stringMessage= Utils.convertFromInputStreamToString(message).toString();
        if(stringCommit.contains(context.getString(R.string.unavailable))){
            stringCommit=String.format(context.getString(R.string.lastcommit),stringCommit);
            stringCommit=stringCommit+" "+context.getText(R.string.lastcommit_unavailable);
        }
        else {
            stringCommit = String.format(context.getString(R.string.lastcommit), stringCommit);
        }
        stringMessage=String.format(stringMessage,stringCommit);
        return stringMessage;
    }

    public void showAlertWithLogoAndVersion(int titleId, CharSequence text, Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setTitle(titleId);
        dialog.setCancelable(true);

        //set up text title
        TextView textTile = (TextView) dialog.findViewById(R.id.aboutTitle);
        textTile.setText(BuildConfig.VERSION_NAME + " (bb)");
        textTile.setGravity(Gravity.RIGHT);

        //set up image view
//        ImageView img = (ImageView) dialog.findViewById(R.id.aboutImage);
//        img.setImageResource(R.drawable.psi);

        //set up text title
        TextView textContent = (TextView) dialog.findViewById(R.id.aboutMessage);
        textContent.setMovementMethod(LinkMovementMethod.getInstance());
        textContent.setText(text);
        //set up button
        Button button = (Button) dialog.findViewById(R.id.aboutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    /**
     * Shows an alert dialog with a given string
     * @param titleId Id of the title resource
     * @param text String of the message
     */
    private void showAlert(int titleId, CharSequence text){
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getApplicationContext().getString(titleId))
                .setMessage(text)
                .setNeutralButton(android.R.string.ok, null).create();
        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
    /**
     * Logs a debug message using current activity SimpleName as tag. Ex:
     *   SurveyActivity => ".SurveyActivity"
     * @param message
     */
    private void debugMessage(String message){
        Log.d("." + this.getClass().getSimpleName(), message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmPush.cancelPushAlarm(this);
    }
}
