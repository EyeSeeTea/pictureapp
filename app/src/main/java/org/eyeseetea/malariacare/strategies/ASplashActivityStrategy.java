package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;


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

}
