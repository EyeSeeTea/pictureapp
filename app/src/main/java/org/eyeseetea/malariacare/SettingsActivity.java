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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.PullController;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.network.ServerInfo;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.AutoCompleteEditTextPreference;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.io.InputStream;
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

    /**
     * Intent extra param that states that the login before changing critical info has been done
     */
    public static final String LOGIN_BEFORE_CHANGE_DONE="LOGIN_BEFORE_CHANGE_DONE";

    /**
     * Intent extra param that states that the login is being done due to an attempt to change the orgunit
     */
    public static final String SETTINGS_CHANGING_ORGUNIT="SETTINGS_CHANGING_ORGUNIT";

    /**
     * Intent extra param that states that the login is being done due to an attempt to change the server
     */
    public static final String SETTINGS_CHANGING_SERVER="SETTINGS_CHANGING_SERVER";
    /**
     * Intent extra param that states that the EULA has been accepted
     */
    public static final String SETTINGS_EULA_ACCEPTED="SETTINGS_EULA_ACCEPTED";


    public static ServerInfo serverInfo;


    protected void onCreate(Bundle savedInstanceState) {
        Dhis2Application.bus.register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop(){
        try{
            Dhis2Application.bus.unregister(this);
        }catch(IllegalArgumentException ex){
            Log.e(TAG,"Unregistering SettingsActivity before it is register");
        }

        super.onStop();
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    void setAutoCompleteEditTextPreference(AutoCompleteEditTextPreference autoCompleteEditTextPreference){
        this.autoCompleteEditTextPreference=autoCompleteEditTextPreference;
    }

    public AutoCompleteEditTextPreference getAutoCompleteEditTextPreference(){
        return this.autoCompleteEditTextPreference;
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
        autoCompleteEditTextPreference.setOnPreferenceClickListener(new LoginRequiredOnPreferenceClickListener(this,true));
        autoCompleteEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                //Save preference new value
                PreferencesState.getInstance().saveStringPreference(R.string.org_unit, newValue.toString());

                // Now, manually update it's value to next value
                // Now, if you click on the item, you'll see the value you've just set here
                preference.setSummary(newValue.toString());

                //Reload preference in memory
                PreferencesState.getInstance().reloadPreferences();

                //Reload orgunits according to server version
                initReloadByServerVersionWhenOrgUnitChanged();

                return true;

            }
        });

//        Preference removeSentPreference = (Preference)findPreference(getApplicationContext().getString(R.string.remove_sent_surveys));
//        removeSentPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                askRemoveSentSurveys();
//                return true;
//            }
//        });

        Preference serverUrlPreference = (Preference)findPreference(getApplicationContext().getResources().getString(R.string.dhis_url));
        serverUrlPreference.setOnPreferenceClickListener(new LoginRequiredOnPreferenceClickListener(this, false));
        serverUrlPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
                initReloadByServerVersionWhenUrlChanged(newValue.toString());

                return true;

            }
        });

        //Check current server version to populate orgunits
        initPopulateOrgUnitsByServerVersion(PreferencesState.getInstance().getDhisURL());

        //XXX Open preference that was being edited, (to review)
        //openClickedPreference();
    }

    /**
     * Opens the preference that is being edited before login
     */
    void openClickedPreference(){
        //No login -> nothing to open
        if(!getIntent().getBooleanExtra(LOGIN_BEFORE_CHANGE_DONE,false)){
            return;
        }

        //Login done -> open right preference
        PreferenceScreen screen = (PreferenceScreen) findPreference("pref_screen");

        //Find key to press
        int key=-1;
        if(getIntent().getBooleanExtra(SETTINGS_CHANGING_ORGUNIT,false)){
            key = R.string.org_unit;
        }
        if(getIntent().getBooleanExtra(SETTINGS_CHANGING_SERVER,false)){
            key = R.string.dhis_url;
        }

        //Nothing to show up
        if(key==-1){
            return;
        }

        //Find order in screen
        int pos = findPreference(getApplicationContext().getString(key)).getOrder();

        //Simulate click on that one
        screen.onItemClick( null, null, pos, 0 );
    }

    /**
     * Checks server version after creating activity (in order to repopulate options in orgunits editor)
     * @param url
     */
    public void initPopulateOrgUnitsByServerVersion(String url){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PreferencesState.getInstance().getContext());
        if(sharedPreferences.getBoolean(PreferencesState.getInstance().getContext().getApplicationContext().getResources().getString(R.string.eula_accepted), false)) {
            CheckServerVersionAsync serverVersionAsync = new CheckServerVersionAsync(this);
            serverVersionAsync.execute(url);
        }
    }

    public void callbackPopulateOrgUnitsByServerVersion(ServerInfo serverInfo){
        Log.d(TAG, "callbackPopulateOrgUnitsByServerVersion " + serverInfo.getVersion());
        autoCompleteEditTextPreference.pullOrgUnits(serverInfo.getVersion());
    }

    /**
     * Launches an async task that resolved the current server version when the org unit has changed.
     */
    private void initReloadByServerVersionWhenOrgUnitChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PreferencesState.getInstance().getContext());
        if(sharedPreferences.getBoolean(PreferencesState.getInstance().getContext().getApplicationContext().getResources().getString(R.string.eula_accepted), false)) {
            CheckServerVersionAsync serverVersionAsync = new CheckServerVersionAsync(this, true, true);
            serverVersionAsync.execute(PreferencesState.getInstance().getDhisURL());
        }
    }


    /**
     * Reloads organisationUnits according to the server version:
     *  - 2.20: Manually via API
     *  - 2.21|2.22: Via sdk (which requires a login to initialize DHIS sdk)
     *  - Else: Error
     */
    private void callbackReloadByServerVersionWhenOrgUnitChanged(ServerInfo serverInfo) {
        Log.d(TAG, "callbackReloadByServerVersionWhenOrgUnitChanged " + serverInfo.getVersion());

        //After changing to a new server survey data is always removed
        PopulateDB.wipeSurveys();

        String serverVersion=serverInfo.getVersion();

        //2.20 -> nothing to do (since orgunits are already loaded)
        if(ServerAPIController.isAPIVersion(serverVersion)){
            return;
        }

        //2.21, 2.22 -> surveys
        if(Constants.DHIS_SDK_221_SERVER.equals(serverVersion) || Constants.DHIS_SDK_222_SERVER.equals(serverVersion)){
            initLoginPrePull(serverInfo);
            return;
        }

        showServerError(serverVersion);
    }

    /**
     * Launches an async task that resolved the current server version.
     * @param url
     */
    private void initReloadByServerVersionWhenUrlChanged(String url) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PreferencesState.getInstance().getContext());
        if(sharedPreferences.getBoolean(PreferencesState.getInstance().getContext().getApplicationContext().getResources().getString(R.string.eula_accepted), false)) {
            Log.d(TAG, "init reload server version when url changed");
            CheckServerVersionAsync serverVersionAsync = new CheckServerVersionAsync(this, true);
            serverVersionAsync.execute(url);
        }
    }

    /**
     * Reloads organisationUnits according to the server version:
     *  - 2.20: Manually via API
     *  - 2.21|2.22: Via sdk (which requires a login to initialize DHIS sdk)
     *  - Else: Error
     */
    public void callbackReloadByServerVersionWhenUrlChanged(ServerInfo serverInfo) {
        Log.d(TAG, "callbackReloadByServerVersionWhenUrlChanged " + serverInfo.getVersion());

        //After changing to a new server survey data is always removed
        PopulateDB.wipeSurveys();

        //And the orgUnit too
        PreferencesState.getInstance().saveStringPreference(R.string.org_unit,"");

        String serverVersion=serverInfo.getVersion();
        Log.d(TAG, "SDK pull start");
        //2.20 -> reload orgunits from server via api
        if(ServerAPIController.isAPIVersion(serverVersion)){
            Log.d(TAG, "Api pull start");
            autoCompleteEditTextPreference.pullOrgUnits(Constants.DHIS_API_SERVER);
            return;
        }

        //2.21, 2.22 -> pull orgunits + surveys
        if(Constants.DHIS_SDK_221_SERVER.equals(serverVersion) || Constants.DHIS_SDK_222_SERVER.equals(serverVersion)){
            Log.d(TAG, "SDK pull start");
            initLoginPrePull(serverInfo);
            return;
        }
        showServerError(serverVersion);


    }

    private void showServerError(String serverVersion) {
        //Other error like: Too many follow-up requests: 21
        if("".equals(serverVersion)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dhis_url_error)
                    .setMessage(R.string.dhis_url_error_generic)
                    .setNeutralButton(android.R.string.yes, null)
                    .create()
                    .show();
            return;
        }
        //Server version not supported -> Error
        new AlertDialog.Builder(this)
                .setTitle(R.string.dhis_url_error)
                .setMessage(R.string.dhis_url_error_bad_version)
                .setNeutralButton(android.R.string.yes, null)
                .create()
                .show();
    }

    /**
     * Logins programatically into new server to initialize sdk api
     * @param serverInfo
     */
    private void initLoginPrePull(ServerInfo serverInfo){
        HttpUrl serverUri = HttpUrl.parse(serverInfo.getUrl());
        Log.d(TAG, "Server url "+serverUri);
        DhisService.logInUser(serverUri, ServerAPIController.getSDKCredentials());
    }

    @Subscribe
    public void callbackLoginPrePull(NetworkJob.NetworkJobResult<ResourceType> result) {
        if(PushController.getInstance().isPushInProgress())
            return;
        //Nothing to check
        if(result==null || result.getResourceType()==null || !result.getResourceType().equals(ResourceType.USERS)){
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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.font_sizes)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.dhis_url)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.org_unit)));

            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            AutoCompleteEditTextPreference autoCompleteEditTextPreference = (AutoCompleteEditTextPreference) findPreference(getString(R.string.org_unit));
            autoCompleteEditTextPreference.setOnPreferenceClickListener(new LoginRequiredOnPreferenceClickListener(settingsActivity, true));
            autoCompleteEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    //Save preference new value
                    PreferencesState.getInstance().saveStringPreference(R.string.org_unit, newValue.toString());

                    // Now, manually update it's value to next value
                    // Now, if you click on the item, you'll see the value you've just set here
                    preference.setSummary(newValue.toString());

                    //Reload preference in memory
                    PreferencesState.getInstance().reloadPreferences();

                    //Reload orgunits according to server version
                    ((SettingsActivity) getActivity()).initReloadByServerVersionWhenOrgUnitChanged();

                    return true;

                }
            });
            settingsActivity.setAutoCompleteEditTextPreference(autoCompleteEditTextPreference);

//            Preference removeSentPreference = (Preference)findPreference(getString(R.string.remove_sent_surveys));
//            removeSentPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                 @Override
//                 public boolean onPreferenceClick(Preference preference) {
//                     askRemoveSentSurveys(getActivity());
//                     return true;
//                 }
//             });

            Preference serverUrlPreference = (Preference)findPreference(getResources().getString(R.string.dhis_url));
            serverUrlPreference.setOnPreferenceClickListener(new LoginRequiredOnPreferenceClickListener(settingsActivity, false));
            serverUrlPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
                    ((SettingsActivity) getActivity()).initReloadByServerVersionWhenUrlChanged(newValue.toString());

                    return true;
                }
            });
            ((SettingsActivity)getActivity()).initPopulateOrgUnitsByServerVersion(PreferencesState.getInstance().getDhisURL());
//            ((SettingsActivity)getActivity()).openClickedPreference();
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
        /**
         * True: The server url has changed, a pull is required
         * False: Need to check server version in order to populate orgunits
         */
        boolean pullRequired;

        /**
         * Flag that tells if this check corresponds to a change in the orgunit
         */
        boolean orgUnitChanged;

        /**
         * Constructor used to check orgunits after activity creation
         * @param context
         */
        public CheckServerVersionAsync(Context context) {
            this.context = context;
            this.pullRequired = false;
        }

        /**
         * Constructor used to pull metadata after url change
         * @param context
         * @param pullRequired
         */
        public CheckServerVersionAsync(Context context,boolean pullRequired) {
            this(context);
            this.pullRequired = pullRequired;
        }

        /**
         * Constructor used to pull events after orgunit change
         * @param context
         * @param pullRequired
         * @param orgUnitChanged
         */
        public CheckServerVersionAsync(Context context,boolean pullRequired, boolean orgUnitChanged) {
            this(context,pullRequired);
            this.orgUnitChanged = orgUnitChanged;
        }

        @Override
        protected void onPreExecute() {}

        protected ServerInfo doInBackground(String... params) {
            serverInfo = new ServerInfo(params[0]);

            PushClient pushClient=new PushClient(context);
            String serverVersion= ServerAPIController.getServerVersion(serverInfo.getUrl());

            serverInfo.setVersion(serverVersion);
            SettingsActivity.serverInfo = serverInfo;
            return serverInfo;
        }

        @Override
        protected void onPostExecute(ServerInfo serverInfo) {

            //OrgUnit has change -> init pull (orgunits not reloaded)
            Log.d(TAG,"org unit changed:"+pullRequired);
            if(orgUnitChanged){
                callbackReloadByServerVersionWhenOrgUnitChanged(serverInfo);
                return;
            }
            Log.d(TAG,"pull required:"+pullRequired);
            //FIXME: review the "pullRequired" variable. There's an inconsistency if we need to make a pull even when pullRequired is false
            //Url has change -> init pull (orgunits reloaded)
            if(pullRequired) {
                callbackReloadByServerVersionWhenUrlChanged(serverInfo);
                //if the server was changed in the first loging it need be set as false
                PreferencesState.getInstance().setIsNewServerUrl(false);
                return;
            } else if(PreferencesState.getInstance().isNewServerUrl()){
                //Url has change on autentication -> init pull (orgunits reloaded)
                PreferencesState.getInstance().setIsNewServerUrl(false);
                callbackReloadByServerVersionWhenUrlChanged(serverInfo);
                return;
            }

            //Orgunits will be reloaded
            callbackPopulateOrgUnitsByServerVersion(serverInfo);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "On activity result");
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_ON_EULA_ACCEPTED) {
            if (data.hasExtra(SettingsActivity.LOGIN_BEFORE_CHANGE_DONE)) {
                Log.d(TAG, "Executing onActivityResult:");
                CheckServerVersionAsync checkServerVersionAsync = new CheckServerVersionAsync(this, true, true);
                checkServerVersionAsync.execute(PreferencesState.getInstance().getDhisURL());
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getApplicationContext().getResources().getString(R.string.eula_accepted), true);
                editor.commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        propagateExtra(intent);
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

/**
 * Listener that moves to the LoginActivity before changing orgunit|server.
 * If login has already been done simply pass through
 */
class LoginRequiredOnPreferenceClickListener implements Preference.OnPreferenceClickListener{

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    /**
     * Flag to indicate if you are changing orgunit or server
     */
    boolean changingOrgUnit;

    LoginRequiredOnPreferenceClickListener(SettingsActivity activity, boolean changingOrgUnit){
        this.activity=activity;
        this.changingOrgUnit=changingOrgUnit;
    }

    /**
     * Shows an alert dialog asking for acceptance of the EULA terms. If ok calls login function, do nothing otherwise
     * @param titleId
     * @param rawId
     * @param context
     */
    public void askEula(int titleId, int rawId, final Context context){
        InputStream message = context.getResources().openRawResource(rawId);
        String stringMessage = Utils.convertFromInputStreamToString(message).toString();
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleId))
                .setMessage(linkedMessage)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchLoginOnEulaAccepted();
            }
                })
                .setNegativeButton(android.R.string.no, null).create();
        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        //Login already done -> move on
        if(isLoginDone()){
            return false;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (!sharedPreferences.getBoolean(activity.getApplicationContext().getResources().getString(R.string.eula_accepted), false)) {
            askEula(R.string.settings_menu_eula, R.raw.eula, activity);
        } else {
            //Launch login with the right extra param
            launchLoginPreChange();
        }

        return true;
    }

    /**
     * Checks if login is required
     * @return
     */
    boolean isLoginDone(){
        return activity.getIntent().getBooleanExtra(SettingsActivity.LOGIN_BEFORE_CHANGE_DONE,false);
    }

    /**
     * Launches Login activity with the right extra params
     */
    void launchLoginPreChange(){
        Intent intent = prepareIntent();
        activity.finish();
        activity.startActivity(intent);
    }
    void launchLoginOnEulaAccepted(){
        Intent intent = prepareIntent();
        intent.putExtra(SettingsActivity.SETTINGS_EULA_ACCEPTED, true);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_ON_EULA_ACCEPTED);
    }

    Intent prepareIntent(){
        String extraKey = changingOrgUnit?SettingsActivity.SETTINGS_CHANGING_ORGUNIT:SettingsActivity.SETTINGS_CHANGING_SERVER;
        Intent intent = new Intent(activity,LoginActivity.class);
        intent.putExtra(extraKey,true);
        return intent;
    }
}