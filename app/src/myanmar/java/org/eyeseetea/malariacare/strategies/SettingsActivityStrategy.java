package org.eyeseetea.malariacare.strategies;

import static android.R.attr.settingsActivity;

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

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    private static final String TAG=".SettingsStrategy";
    LogoutAndLoginRequiredOnPreferenceClickListener loginRequiredOnPreferenceClickListener;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        loginRequiredOnPreferenceClickListener = new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    @Override
    public void onCreate() {
        //Register into sdk bug for listening to logout events
        Dhis2Application.bus.register(this);
    }

    @Override
    public void onStop() {
        try {
            //Unregister from bus before leaving
            Dhis2Application.bus.unregister(this);
        }catch(Exception e){}
    }



    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return loginRequiredOnPreferenceClickListener;
    }

    @Subscribe
    public void onLogoutFinished(UiEvent uiEvent){
        //No event or not a logout event -> done
        if(uiEvent==null || !uiEvent.getEventType().equals(UiEvent.UiEventType.USER_LOG_OUT)){
            return;
        }
        Log.i(TAG, "Logging out from sdk...OK");
        Session.logout();
        Intent loginIntent = new Intent(settingsActivity,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //finish();
        settingsActivity.startActivity(loginIntent);
    }

}

/**
 * Listener that moves to the LoginActivity before changing DHIS config
 */
class LogoutAndLoginRequiredOnPreferenceClickListener implements Preference.OnPreferenceClickListener{

    private static final String TAG="LoginPreferenceListener";

    /**
     * Reference to the activity so you can use this from the activity or the fragment
     */
    SettingsActivity activity;

    LogoutAndLoginRequiredOnPreferenceClickListener(SettingsActivity activity){
        this.activity=activity;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.settings_menu_logout_title))
                .setMessage(activity.getString(R.string.settings_menu_logout_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //finish activity and go to login
                        Log.i(TAG, "Logging out from sdk...");
                        DhisService.logOutUser(activity);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
        Log.i(TAG, "Returning from dialog -> true");
        return true;
    }
}
