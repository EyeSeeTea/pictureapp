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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.strategies.SettingsActivityStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.AutoCompleteEditTextPreference;
import org.eyeseetea.sdk.presentation.styles.FontStyle;

import java.util.ArrayList;
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
     * Extra param to annotate the activity to return after settings
     */
    public static final String SETTINGS_CALLER_ACTIVITY = "SETTINGS_CALLER_ACTIVITY";

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
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
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
        PreferencesState.getInstance().onCreateActivityPreferences(getResources(), getTheme());
        mSettingsActivityStrategy.onCreate();
    }

    public void restartActivity() {
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

        setupPreferencesScreen();
    }

    private void setupPreferencesScreen() {

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

        loadFontStyleListPreference();

        autoCompleteEditTextPreference = (AutoCompleteEditTextPreference) findPreference(
                getApplicationContext().getString(R.string.org_unit));
        autoCompleteEditTextPreference.setOnPreferenceClickListener(
                mSettingsActivityStrategy.getOnPreferenceClickListener());
        autoCompleteEditTextPreference.pullOrgUnits();

        autoCompleteEditTextPreference.setContext(this);
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

        mSettingsActivityStrategy.addExtraPreferences();
    }

    private void loadFontStyleListPreference() {
        ListPreference listPreference = (ListPreference) findPreference(
                getApplicationContext().getString(R.string.font_sizes));

        List<String> entries = new ArrayList<>();
        List<String> entryValues = new ArrayList<>();

        for (FontStyle fontStyle:FontStyle.values()) {
            entries.add(Utils.getInternationalizedString(fontStyle.getTitle()));
            entryValues.add(String.valueOf(fontStyle.getResId()));
        }

        listPreference.setEntries(entries.toArray(new CharSequence[entries.size()]));
        listPreference.setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));
        listPreference.setDefaultValue(String.valueOf(FontStyle.Medium.getResId()));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.language_code))) {
            restartActivity();
        }
        mSettingsActivityStrategy.onSharedPreferenceChanged(sharedPreferences, key);
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
        mSettingsActivityStrategy.onBackPressed();
        PreferencesState.getInstance().reloadPreferences();
        Class callerActivityClass = getCallerActivity();
        Intent returnIntent = new Intent(this, callerActivityClass);
        returnIntent.putExtra(getString(R.string.show_announcement_key), SettingsActivityStrategy.showAnnouncementOnBackPressed());
        startActivity(returnIntent);
    }

    private Class getCallerActivity() {
        //FIXME Not working as it should the intent param is always null
        Intent creationIntent = getIntent();
        if (creationIntent == null) {
            return DashboardActivity.class;
        }
        Class callerActivity = (Class) creationIntent.getSerializableExtra(
                SETTINGS_CALLER_ACTIVITY);
        if (callerActivity == null) {
            return DashboardActivity.class;
        }

        return callerActivity;
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

    @Override
    protected void onStart() {
        mSettingsActivityStrategy.onStart();
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mSettingsActivityStrategy.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        mSettingsActivityStrategy.onDestroy();
        super.onDestroy();
    }
}