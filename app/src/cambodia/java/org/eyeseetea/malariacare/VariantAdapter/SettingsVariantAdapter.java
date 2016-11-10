package org.eyeseetea.malariacare.VariantAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.database.utils.Session;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

import static android.R.attr.settingsActivity;

public class SettingsVariantAdapter extends ASettingsVariantAdapter {

    private static final String TAG=".SettingsVariantAdapter";
    LoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsVariantAdapter(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener = new LoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }



    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

}

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
class LoginRequiredOnPreferenceClickListener implements Preference.OnPreferenceClickListener{

    private static final String TAG="LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    LoginRequiredOnPreferenceClickListener(SettingsActivity activity){
        this.activity=activity;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent loginIntent = new Intent(activity,LoginActivity.class);
        //finish();
        activity.startActivity(loginIntent);
        return true;
    }
}
