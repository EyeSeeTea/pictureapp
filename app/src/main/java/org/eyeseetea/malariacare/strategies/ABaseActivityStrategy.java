package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public abstract class ABaseActivityStrategy {
    protected BaseActivity mBaseActivity;
    protected static String TAG = ".BaseActivityStrategy";
    public ABaseActivityStrategy(BaseActivity baseActivity) {
        this.mBaseActivity = baseActivity;
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void onCreateOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public void hideMenuItems(Menu menu) {
    }

    public abstract void onBackPressed();

    public abstract void onWindowFocusChanged(boolean hasFocus);

    public void goSettings() {
        Intent intentSettings = new Intent(mBaseActivity, SettingsActivity.class);
        intentSettings.putExtra(SettingsActivity.SETTINGS_CALLER_ACTIVITY, this.getClass());
        intentSettings.putExtra(SettingsActivity.IS_LOGIN_DONE, false);
        mBaseActivity.startActivity(new Intent(mBaseActivity, SettingsActivity.class));
    }

    public void onDestroy() {

    }
}
