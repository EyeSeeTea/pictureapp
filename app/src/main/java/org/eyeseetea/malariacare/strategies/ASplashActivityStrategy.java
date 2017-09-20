package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;

import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;


public abstract class ASplashActivityStrategy {

    protected Activity activity;

    public ASplashActivityStrategy(Activity activity) {
        this.activity = activity;
    }

    public abstract void finishAndGo();

    protected void finishAndGo(Class<? extends Activity> activityClass) {
        activity.startActivity(new Intent(activity, activityClass));
        activity.finish();
    }
    public void initPullFilters(PullFilters pullFilters){
        pullFilters.setDemo(true);
    }

}
