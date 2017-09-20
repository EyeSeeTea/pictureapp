package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(DashboardActivity.class);
    }

    @Override
    public void initPullFilters(PullFilters pullFilters) {
        pullFilters.setAutoConfig(true);
    }
}