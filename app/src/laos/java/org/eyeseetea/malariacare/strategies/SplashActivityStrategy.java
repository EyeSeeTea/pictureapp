package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import org.eyeseetea.malariacare.DashboardActivity;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity
    public SplashActivityStrategy(Activity mActivity, final SplashScreenActivity.Callback callback) {
        super(mActivity);
        callback.onSuccess();
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(DashboardActivity.class);
    }
}