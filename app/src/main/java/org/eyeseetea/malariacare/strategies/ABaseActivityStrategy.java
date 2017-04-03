package org.eyeseetea.malariacare.strategies;

import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;

public abstract class ABaseActivityStrategy {
    protected BaseActivity mBaseActivity;
    protected static String TAG = ".BaseActivityStrategy";
    public ABaseActivityStrategy(BaseActivity baseActivity) {
        this.mBaseActivity = baseActivity;
    }

    public abstract void onStop();

    public abstract void onCreate();

    public abstract void onCreateOptionsMenu(Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public void hideMenuItems(Menu menu) {
    }
}
