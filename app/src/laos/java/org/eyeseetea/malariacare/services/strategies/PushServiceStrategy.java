package org.eyeseetea.malariacare.services.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {
    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {
        Log.d(TAG, "execute push");
        executePush();
    }
}
