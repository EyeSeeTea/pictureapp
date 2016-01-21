/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Survelliance App.
 *
 *  QIS Survelliance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Survelliance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Survelliance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.ServerInfo;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.views.AutoCompleteEditTextPreference;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.network.Credentials;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static final String TAG = ".Settings";
    private AutoCompleteEditTextPreference autoCompleteEditTextPreference;


    protected void onCreate(Bundle savedInstanceState) {
        Dhis2Application.bus.register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop(){
        try {
            Dhis2Application.bus.unregister(this);
        }catch(Exception e){}
        super.onStop();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference(getApplicationContext().getString(R.string.font_sizes)));
        bindPreferenceSummaryToValue(findPreference(getApplicationContext().getString(R.string.dhis_url)));
        bindPreferenceSummaryToValue(findPreference(getApplicationContext().getString(R.string.org_unit)));

        // Set the ClickListener to the android:key"remove_sent_surveys" preference.

        autoCompleteEditTextPreference= (AutoCompleteEditTextPreference) findPreference(getApplicationContext().getString(R.string.org_unit));
        Preference button = (Preference)findPreference(getApplicationContext().getString(R.string.remove_sent_surveys));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                askRemoveSentSurveys();
                return true;
            }
        });

        Preference button2 = (Preference)findPreference(getApplicationContext().getResources().getString(R.string.dhis_url));
        button2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                //Save preference new value
                PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, newValue.toString());

                // Now, manually update it's value to next value
                // Now, if you click on the item, you'll see the value you've just set here
                preference.setSummary(newValue.toString());

                //Reload preference in memory
                PreferencesState.getInstance().reloadPreferences();

                //Reload orgunits according to server version
                initReloadAccordingToServerVersion(newValue.toString());

                return true;

            }
        });
    }

    /**
     * Launches an async task that resolved the current server version.
     * @param url
     */
    private void initReloadAccordingToServerVersion(String url){
        CheckServerVersionAsync serverVersionAsync = new CheckServerVersionAsync(this);
        serverVersionAsync.execute(url);
    }

    /**
     * Reloads organisationUnits according to the server version:
     *  - 2.20: Manually via API
     *  - 2.21|2.22: Via sdk (which requires a login to initialize DHIS sdk)
     *  - Else: Error
     */
    private void callbackReloadAccordingToServerVersion(ServerInfo serverInfo) {
        Log.d(TAG, "callbackReloadAccordingToServerVersion " + serverInfo.getVersion());
        PreferencesState.getInstance().saveStringPreference(R.string.server_version, serverInfo.getVersion());
        switch (serverInfo.getVersion()){
            case "2.20":
                PushClient.newOrgUnitOrServer();
                autoCompleteEditTextPreference.pullOrgUnits();
                break;
            case "2.21":
            case "2.22":
                //Pull from sdk
                initLoginPrePull(serverInfo);
                break;
            default:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dhis_url_error)
                        .setMessage(R.string.dhis_url_error_bad_version)
                        .setNeutralButton(android.R.string.yes, null)
                        .create()
                        .show();
        }
    }

    /**
     * Logins programatically into new server to initialize sdk api
     * @param serverInfo
     */
    private void initLoginPrePull(ServerInfo serverInfo){
        HttpUrl serverUri = HttpUrl.parse(serverInfo.getUrl());
        DhisService.logInUser(serverUri, PushClient.getSDKCredentials());
    }

    @Subscribe
    public void callbackLoginPrePull(NetworkJob.NetworkJobResult<ResourceType> result) {
        //Nothing to check
        if(result==null || !result.getResourceType().equals(ResourceType.USERS)){
            return;
        }

        //Login failed
        if(result.getResponseHolder().getApiException()!=null){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dhis_url_error)
                    .setMessage(R.string.dhis_url_error_bad_credentials)
                    .setNeutralButton(android.R.string.yes, null)
                    .create()
                    .show();
        }

        //Login successful start reload
        finish();
        startActivity(new Intent(this, ProgressActivity.class));
    }

    //ask if the Sent Surveys
    private void askRemoveSentSurveys() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        removeSentSurveys();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_title_delete_surveys).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void removeSentSurveys() {
        Intent surveysIntent=new Intent(this, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.REMOVE_SENT_SURVEYS_ACTION);
        this.startService(surveysIntent);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private AutoCompleteEditTextPreference autoCompleteEditTextPreference;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.font_sizes)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.dhis_url)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.org_unit)));


            autoCompleteEditTextPreference= (AutoCompleteEditTextPreference) findPreference(getString(R.string.org_unit));

            Preference button = (Preference)findPreference(getString(R.string.remove_sent_surveys));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    askRemoveSentSurveys(getActivity());
                    return true;
                }
            });

            Preference button2 = (Preference)findPreference(getResources().getString(R.string.dhis_url));

            button2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //Save preference new value
                    PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, newValue.toString());

                    // Now, manually update it's value to next value
                    // Now, if you click on the item, you'll see the value you've just set here
                    preference.setSummary(newValue.toString());

                    //Reload preference in memory
                    PreferencesState.getInstance().reloadPreferences();

                    //Reload orgunits according to server version
                    ((SettingsActivity)getActivity()).initReloadAccordingToServerVersion(newValue.toString());

                    return true;
                }
            });
    }
    }

    //asks the user whether to delete the surveys sent
    private static void askRemoveSentSurveys(final Activity activity) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        removeSentSurveys(activity);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        builder.setMessage(R.string.dialog_title_delete_surveys).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }


    private static void removeSentSurveys(Activity activity) {
        Intent surveysIntent=new Intent(activity, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.REMOVE_SENT_SURVEYS_ACTION);
        activity.startService(surveysIntent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isValidFragment(String fragment){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        //Reload changes into PreferencesState
        PreferencesState.getInstance().reloadPreferences();
    }

    @Override
    public void onBackPressed() {
        PreferencesState.getInstance().reloadPreferences();
        PushClient.newOrgUnitOrServer();
        Class callerActivityClass=getCallerActivity();
        Intent returnIntent=new Intent(this,callerActivityClass);
        startActivity(returnIntent);
    }

    private Class getCallerActivity(){
        //FIXME Not working as it should the intent param is always null
        Intent creationIntent=getIntent();
        if(creationIntent==null){
            return DashboardActivity.class;
        }
        Class callerActivity=(Class)creationIntent.getSerializableExtra(BaseActivity.SETTINGS_CALLER_ACTIVITY);
        if(callerActivity==null){
            return DashboardActivity.class;
        }

        return callerActivity;
    }

    /**
     * AsyncTask that resolves the server version before pulling orgunits from it
     */
    class CheckServerVersionAsync extends AsyncTask<String, Void, ServerInfo> {

        Context context;
        ServerInfo serverInfo;

        public CheckServerVersionAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {}

        protected ServerInfo doInBackground(String... params) {
            serverInfo = new ServerInfo(params[0]);

            PushClient pushClient=new PushClient(context);
            String serverVersion=pushClient.getServerVersion(serverInfo.getUrl());

            serverInfo.setVersion(serverVersion);
            return serverInfo;
        }

        @Override
        protected void onPostExecute(ServerInfo serverInfo) {
            callbackReloadAccordingToServerVersion(serverInfo);
        }

    }

}
