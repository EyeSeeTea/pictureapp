package org.eyeseetea.malariacare.variantadapter;


import android.preference.Preference;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public abstract class AProgressVariantAdapter {

    protected ProgressActivity progressActivity;

    public AProgressVariantAdapter(ProgressActivity progressActivity){
        this.progressActivity = progressActivity;
    }


    public abstract void finishAndGo();
}
