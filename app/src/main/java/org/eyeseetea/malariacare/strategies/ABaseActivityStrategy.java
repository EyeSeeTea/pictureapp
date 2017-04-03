package org.eyeseetea.malariacare.strategies;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;

public abstract class ABaseActivityStrategy {
    protected BaseActivity mBaseActivity;

    public ABaseActivityStrategy(BaseActivity baseActivity) {
        this.mBaseActivity = baseActivity;
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void onCreateOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public abstract void onBackPressed();

    public abstract void onWindowFocusChanged(boolean hasFocus);

    public abstract void goSettings();

    public abstract  void onActivityResult(int requestCode, int resultCode, Intent data);
}
