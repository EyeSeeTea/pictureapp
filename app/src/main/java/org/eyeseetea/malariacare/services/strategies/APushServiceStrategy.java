package org.eyeseetea.malariacare.services.strategies;


import org.eyeseetea.malariacare.services.PushService;

public abstract class APushServiceStrategy {

    protected PushService mPushService;

    public APushServiceStrategy(PushService pushService) {
        mPushService = pushService;
    }

    public abstract void push();
}
