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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.strategies.SettingsActivityStrategy;
import org.eyeseetea.malariacare.views.AutoCompleteEditTextPreference;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String IS_LOGIN_DONE = "IS_LOGIN_DONE";
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private static final String TAG = ".SettingsActivity";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
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
    public SettingsActivityStrategy mSettingsActivityStrategy = new SettingsActivityStrategy(this);
    public AutoCompleteEditTextPreference autoCompleteEditTextPreference;
    public Preference serverUrlPreference;

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

    /**
     * Sets the application languages and populate the language in the preference
     */
    private static void setLanguageOptions(Preference preference) {
        ListPreference listPreference = (ListPreference) preference;

        HashMap<String, String> languages = getAppLanguages(R.string.system_defined);

        CharSequence[] newEntries = new CharSequence[languages.size() + 1];
        CharSequence[] newValues = new CharSequence[languages.size() + 1];
        int i = 0;
        newEntries[i] = PreferencesState.getInstance().getContext().getString(
                R.string.system_defined);
        newValues[i] = "";
        for (String language : languages.keySet()) {
            i++;
            String languageCode = languages.get(language);
            String firstLetter = language.substring(0, 1).toUpperCase();
            language = firstLetter + language.substring(1, language.length());
            newEntries[i] = language;
            newValues[i] = languageCode;
        }

        listPreference.setEntries(newEntries);
        listPreference.setEntryValues(newValues);
    }

    /**
     * This method finds the existing app translations
     * * @param stringId this string id should be different in all value-xx/string.xml files. Else
     * the language can be ignored
     */
    public static HashMap<String, String> getAppLanguages(int stringId) {
        HashMap<String, String> languages = new HashMap<>();
        Context context = PreferencesState.getInstance().getContext();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Resources r = context.getResources();
        Configuration c = r.getConfiguration();
        String[] loc = r.getAssets().getLocales();
        for (int i = 0; i < loc.length; i++) {
            c.locale = new Locale(loc[i]);
            Resources res = new Resources(context.getAssets(), metrics, c);
            String s1 = res.getString(stringId);
            String language = c.locale.getDisplayLanguage();
            c.locale = new Locale("");
            Resources res2 = new Resources(context.getAssets(), metrics, c);
            String s2 = res2.getString(stringId);

            //Compare with the default language
            if (!s1.equals(s2)) {
                languages.put(language, loc[i]);
            }
        }
        return languages;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsActivityStrategy.onCreate();
        PreferencesState.getInstance().loadsLanguageInActivity();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onStop() {
        mSettingsActivityStrategy.onStop();

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


        if (BuildConfig.translations) {
            setLanguageOptions(
                    findPreference(getApplicationContext().getString(R.string.language_code)));
        }

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        if (BuildConfig.translations) {
            bindPreferenceSummaryToValue(
                    findPreference(getApplicationContext().getString(R.string.language_code)));
        }

        bindPreferenceSummaryToValue(
                findPreference(getApplicationContext().getString(R.string.font_sizes)));
        bindPreferenceSummaryToValue(
                findPreference(getApplicationContext().getString(R.string.dhis_url)));
        bindPreferenceSummaryToValue(
                findPreference(getApplicationContext().getString(R.string.org_unit)));

        autoCompleteEditTextPreference = (AutoCompleteEditTextPreference) findPreference(
                getApplicationContext().getString(R.string.org_unit));
        autoCompleteEditTextPreference.setOnPreferenceClickListener(
                mSettingsActivityStrategy.getOnPreferenceClickListener());
        autoCompleteEditTextPreference.pullOrgUnits();

        serverUrlPreference = (Preference) findPreference(
                getApplicationContext().getResources().getString(R.string.dhis_url));
        serverUrlPreference.setOnPreferenceClickListener(
                mSettingsActivityStrategy.getOnPreferenceClickListener());

        mSettingsActivityStrategy.setupPreferencesScreen(getPreferenceScreen());

        if (mSettingsActivityStrategy.getOnPreferenceChangeListener() != null) {
            serverUrlPreference.setOnPreferenceChangeListener(
                    mSettingsActivityStrategy.getOnPreferenceChangeListener());

            autoCompleteEditTextPreference.setOnPreferenceChangeListener(
                    mSettingsActivityStrategy.getOnPreferenceChangeListener());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.language_code))) {
            restartActivity();
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isValidFragment(String fragment) {
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
        Class callerActivityClass = getCallerActivity();
        Intent returnIntent = new Intent(this, callerActivityClass);
        startActivity(returnIntent);
    }

    private Class getCallerActivity() {
        //FIXME Not working as it should the intent param is always null
        Intent creationIntent = getIntent();
        if (creationIntent == null) {
            return DashboardActivity.class;
        }
        Class callerActivity = (Class) creationIntent.getSerializableExtra(
                BaseActivity.SETTINGS_CALLER_ACTIVITY);
        if (callerActivity == null) {
            return DashboardActivity.class;
        }

        return callerActivity;
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


            if (BuildConfig.translations) {
                setLanguageOptions(findPreference(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.language_code)));
            }

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.font_sizes)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.language_code)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.dhis_url)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.org_unit)));

            SettingsActivity settingsActivity = (SettingsActivity) getActivity();

            //Hide translation option if is not active in gradle variable
            if (BuildConfig.translations) {
                bindPreferenceSummaryToValue(
                        findPreference(getResources().getString(R.string.language_code)));
            }

            settingsActivity.autoCompleteEditTextPreference =
                    (AutoCompleteEditTextPreference) findPreference(getString(R.string.org_unit));
            settingsActivity.serverUrlPreference = (Preference) findPreference(
                    getResources().getString(R.string.dhis_url));

            settingsActivity.autoCompleteEditTextPreference.pullOrgUnits();

            settingsActivity.autoCompleteEditTextPreference.setOnPreferenceClickListener(
                    settingsActivity.mSettingsActivityStrategy.getOnPreferenceClickListener());
            settingsActivity.serverUrlPreference.setOnPreferenceClickListener(
                    settingsActivity.mSettingsActivityStrategy.getOnPreferenceClickListener());

            settingsActivity.mSettingsActivityStrategy.setupPreferencesScreen(
                    getPreferenceScreen());

            if (settingsActivity.mSettingsActivityStrategy.getOnPreferenceChangeListener()
                    != null) {
                settingsActivity.serverUrlPreference.setOnPreferenceChangeListener(
                        settingsActivity.mSettingsActivityStrategy.getOnPreferenceChangeListener());

                settingsActivity.autoCompleteEditTextPreference.setOnPreferenceChangeListener(
                        settingsActivity.mSettingsActivityStrategy.getOnPreferenceChangeListener());
            }
        }
    }

}