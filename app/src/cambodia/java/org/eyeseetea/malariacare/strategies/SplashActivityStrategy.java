package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.SplashScreenActivity;
import org.eyeseetea.malariacare.data.remote.SdkQueries;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(DashboardActivity.class);
    }

    @Override
    public void init(SplashScreenActivity.Callback callback) {
        SdkQueries.createDBIndexes();
        super.init(callback);
    }
}