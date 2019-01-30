package org.eyeseetea.malariacare.idling_resources;


import android.app.ActivityManager;
import android.content.Context;
import android.support.test.espresso.IdlingResource;

public class IntentTaskIdlingResource implements IdlingResource {
    private final Context context;
    private ResourceCallback resourceCallback;
    private String expectedClass;

    public IntentTaskIdlingResource(Context context, String canonicalName) {
        this.context = context;
        this.expectedClass = canonicalName;
    }

    @Override
    public String getName() {
        return IntentTaskIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !isIntentTaskRunning();
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    private boolean isIntentTaskRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningTaskInfo info : manager.getRunningTasks(Integer.MAX_VALUE)) {
            if (expectedClass.equals(info.baseActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
