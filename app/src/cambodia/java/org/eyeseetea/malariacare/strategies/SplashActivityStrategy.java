package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import org.eyeseetea.malariacare.DashboardActivity;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(DashboardActivity.class);
    }
}