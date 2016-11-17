package org.eyeseetea.malariacare.strategies;

import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
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

}
