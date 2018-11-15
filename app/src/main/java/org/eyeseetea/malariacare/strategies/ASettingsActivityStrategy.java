package org.eyeseetea.malariacare.strategies;


import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.styles.FontStyle;

import java.util.List;

public abstract class ASettingsActivityStrategy {

    protected static final String TAG = ".SettingsActivity";

    protected SettingsActivity settingsActivity;

    public ASettingsActivityStrategy(SettingsActivity settingsActivity) {
        this.settingsActivity = settingsActivity;
    }

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void setupPreferencesScreen(PreferenceScreen preferenceScreen);

    public abstract Preference.OnPreferenceClickListener getOnPreferenceClickListener();

    public abstract Preference.OnPreferenceChangeListener getOnPreferenceChangeListener();

    public abstract void addExtraPreferences();

    public abstract void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);


    public abstract void onStart();

    public abstract void onBackPressed();

    public abstract void onWindowFocusChanged(boolean hasFocus);

    public void onDestroy() {

    }

    public void pullAfterChangeOuFlags() {
        PreferencesState.getInstance().setMetaDataDownload(false);
        PreferencesState.getInstance().setPullDataAfterMetadata(true);
        PreferencesState.getInstance().setDataLimitedByPreferenceOrgUnit(true);
    }

    public void addFontStyleEntries(List<String> entries,List<String> entryValues){
        for (FontStyle fontStyle:FontStyle.values()) {
            entries.add(Utils.getInternationalizedString(fontStyle.getTitle()));
            entryValues.add(String.valueOf(fontStyle.getResId()));
        }
    }

    public void setLanguageOptions(Preference preference){
        ListPreference listPreference = (ListPreference) preference;
        listPreference.setEntries(R.array.languages_strings);
        listPreference.setEntryValues(R.array.languages_codes);
    }
}
