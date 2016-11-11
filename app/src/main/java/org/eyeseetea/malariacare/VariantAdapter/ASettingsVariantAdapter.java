package org.eyeseetea.malariacare.variantadapter;


import android.preference.Preference;

import org.eyeseetea.malariacare.SettingsActivity;

public abstract class ASettingsVariantAdapter {

    protected SettingsActivity settingsActivity;

    public ASettingsVariantAdapter(SettingsActivity settingsActivity){
        this.settingsActivity = settingsActivity;
    }

    public abstract void onPause();
    public abstract void onStop();
    public abstract void onCreate();

    public abstract Preference.OnPreferenceClickListener getOnPreferenceClickListener();
}
