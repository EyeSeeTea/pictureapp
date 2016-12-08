package org.eyeseetea.malariacare.services.strategies;

import org.eyeseetea.malariacare.domain.usecase.PushSurveysUseCase;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {
    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {
        PushSurveysUseCase pushSurveysUseCase = new PushSurveysUseCase(mPushService);

        pushSurveysUseCase.execute(new PushSurveysUseCase.Callback() {
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
