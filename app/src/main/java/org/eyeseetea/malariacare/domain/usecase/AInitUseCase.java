package org.eyeseetea.malariacare.domain.usecase;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by idelcano on 12/12/2016.
 */

public abstract class AInitUseCase {

    protected Activity activity;

    public AInitUseCase(Activity activity) {
        this.activity = activity;
    }

    public abstract void finishAndGo();

    public void finishAndGo(Class<? extends Activity> activityClass) {
        activity.startActivity(new Intent(activity, activityClass));
        activity.finish();
    }

}
