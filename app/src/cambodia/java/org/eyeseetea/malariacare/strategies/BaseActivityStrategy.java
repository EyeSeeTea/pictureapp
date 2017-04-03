package org.eyeseetea.malariacare.strategies;

import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void goSettings() {
        Intent intentSettings = new Intent(mBaseActivity, SettingsActivity.class);
        intentSettings.putExtra(SettingsActivity.SETTINGS_CALLER_ACTIVITY, this.getClass());
        intentSettings.putExtra(SettingsActivity.IS_LOGIN_DONE, false);
        mBaseActivity.startActivity(new Intent(mBaseActivity, SettingsActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
