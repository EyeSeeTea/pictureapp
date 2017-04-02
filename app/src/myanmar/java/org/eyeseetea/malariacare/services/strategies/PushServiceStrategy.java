package org.eyeseetea.malariacare.services.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.usecase.push.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {

    public PushServiceStrategy(PushService pushService) {
        super(pushService);
    }

    @Override
    public void push() {
        if (Session.getCredentials().isDemoCredentials()) {
            Log.d(TAG, "execute mocked push");
            executeMockedPush();
        } else {
            Log.d(TAG, "execute push");
            executePush();
        }
    }

    protected void executeMockedPush() {
        MockedPushSurveysUseCase mockedPushSurveysUseCase = new MockedPushSurveysUseCase();

        mockedPushSurveysUseCase.execute(new MockedPushSurveysUseCase.Callback() {
            @Override
            public void onPushFinished() {
                Log.d(TAG, "onPushMockFinished");
                mPushService.onPushFinished();
            }
        });
    }
}
