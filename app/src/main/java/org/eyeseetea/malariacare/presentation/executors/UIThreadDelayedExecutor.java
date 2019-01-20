package org.eyeseetea.malariacare.presentation.executors;

import android.os.Handler;
import android.os.Looper;

import org.eyeseetea.malariacare.domain.boundary.executors.IDelayedMainExecutor;


public class UIThreadDelayedExecutor implements IDelayedMainExecutor {

    private Handler handler;

    public UIThreadDelayedExecutor() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void postDelayed(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }
}