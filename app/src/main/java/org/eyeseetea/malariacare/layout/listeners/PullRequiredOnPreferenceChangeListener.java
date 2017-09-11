package org.eyeseetea.malariacare.layout.listeners;


import android.preference.Preference;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class PullRequiredOnPreferenceChangeListener implements
        Preference.OnPreferenceChangeListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        //Save preference new value
        PreferencesState.getInstance().saveStringPreference(preference.getKey(),
                newValue.toString());

        // Now, manually update it's value to next value
        // Now, if you click on the item, you'll see the value you've just set here
        preference.setSummary(newValue.toString());

        //Reload preference in memory
        PreferencesState.getInstance().reloadPreferences();

        if (preferenceIsDhisUrl(preference)) {
            PreferencesState.getInstance().saveStringPreference(R.string.org_unit, "");
        }

        return true;

    }

    private boolean preferenceIsDhisUrl(Preference preference) {
        return preference.getKey() == preference.getContext().getResources().getString(
                R.string.dhis_url);
    }
}
