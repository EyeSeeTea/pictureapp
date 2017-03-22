package org.eyeseetea.malariacare.services.strategies;


import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.PushController;
import org.eyeseetea.malariacare.domain.usecase.MockedPushSurveysUseCase;
import org.eyeseetea.malariacare.domain.usecase.PushUseCase;
import org.eyeseetea.malariacare.network.SurveyChecker;
import org.eyeseetea.malariacare.services.PushService;

public abstract class APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";

    protected PushService mPushService;

    public APushServiceStrategy(PushService pushService) {
        mPushService = pushService;
    }

    public abstract void push();

    protected void executePush() {
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

            @Override
            public void onInformativeError(String message) {
                showInDialog(message);
            }
        });
    }

    private void onError(String error) {
        Log.e(TAG, error);
        mPushService.onPushError(error);
    }

    public void showInDialog(String message) {
        DashboardActivity.dashboardActivity.showException(
                PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title), message);
    }
}
