package org.eyeseetea.malariacare.services.strategies;

import android.content.Context;
import org.eyeseetea.malariacare.domain.usecase.PushUseCase;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy{
    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {
        PushUseCase pushUseCase = new PushUseCase(mPushService);

        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushFinished() {
                mPushService.onPushFinished();
            }

            @Override
            public void onPushError(String message) {
                mPushService.onPushError(message);
            }
        });
    }
}
