package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

/**
 * Created by manuel on 28/12/16.
 */

public abstract class ADashboardActivityStrategy {

    public abstract void reloadStockFragment(Activity activity);

    public abstract boolean showStockFragment(Activity activity, boolean isMoveToLeft);

    public abstract boolean isHistoricNewReceiptBalanceFragment(Activity activity);


}
