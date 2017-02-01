package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

/**
 * Created by manuel on 28/12/16.
 */

public class DashboardActivityStrategy extends ADashboardActivityStrategy {
    @Override
    public void reloadStockFragment(Activity activity) {

    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        return false;
    }
}
