package org.eyeseetea.malariacare.services.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.exporter.PushController;
import org.eyeseetea.malariacare.domain.usecase.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.domain.usecase.PushUseCase;
import org.eyeseetea.malariacare.network.SurveyChecker;
import org.eyeseetea.malariacare.services.PushService;

public class PushServiceStrategy extends APushServiceStrategy {
    public static final String TAG = ".PushServiceStrategy";

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

    private void executeMockedPush() {
        MockedPushSurveysUseCase mockedPushSurveysUseCase = new MockedPushSurveysUseCase();

        mockedPushSurveysUseCase.execute(new MockedPushSurveysUseCase.Callback() {
            @Override
            public void onPushFinished() {
                Log.d(TAG, "onPushMockFinished");
                mPushService.onPushFinished();
            }
        });
    }

    private void executePush() {
        PushController pushController = new PushController(mPushService);
        PushUseCase pushUseCase = new PushUseCase(pushController);

        SurveyChecker.launchQuarantineChecker();

        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "push complete");
                mPushService.onPushFinished();
            }

            @Override
            public void onPushInProgressError() {
                onError("Push stopped, There is already a push in progress");
            }

            @Override
            public void onPushError() {
                onError("Unexpected error has occurred in push process");
            }

            @Override
            public void onSurveysNotFoundError() {
                onError("Pending surveys not found");
            }

            @Override
            public void onConversionError() {
                onError("An error has occurred to the conversion in push process");
            }

            @Override
            public void onNetworkError() {
                onError("Network not available");
            }
        });
    }

    private void onError(String error) {
        Log.e(TAG, error);
        mPushService.onPushError(error);
    }
}
