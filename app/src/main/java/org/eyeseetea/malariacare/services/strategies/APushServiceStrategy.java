package org.eyeseetea.malariacare.services.strategies;


import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.factories.SyncFactoryStrategy;
import org.eyeseetea.malariacare.services.PushService;

public abstract class APushServiceStrategy {

    public static final String TAG = ".PushServiceStrategy";

    protected PushService mPushService;

    public APushServiceStrategy(PushService pushService) {
        mPushService = pushService;
    }

    public abstract void push();

    protected void executePush() {
        PushUseCase pushUseCase =
                new SyncFactoryStrategy().getPushUseCase(mPushService.getApplicationContext());

        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {
                Log.d(TAG, "On push start");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "PUSHUSECASE WITHOUT ERROR push complete");
                mPushService.onPushFinished();
            }

            @Override
            public void onPushInProgressError() {
                Log.d(TAG, "PUSHUSECASE ERROR Push stopped, There is already a push in progress");
            }

            @Override
            public void onPushError() {
                onError("PUSHUSECASE ERROR Unexpected error has occurred in push process");
            }

            @Override
            public void onSurveysNotFoundError() {
                onError("PUSHUSECASE ERROR Pending surveys not found");}

            @Override
            public void onConversionError() {
                onError("PUSHUSECASE ERROR An error has occurred to the conversion in push process");
            }

            @Override
            public void onNetworkError() {
                onError("PUSHUSECASE ERROR Network not available");}

            @Override
            public void onInformativeError(String message) {
                showInDialog(PreferencesState.getInstance().getContext().getString(
                        R.string.error_conflict_title), "PUSHUSECASE ERROR "+message + PreferencesState.getInstance().isPushInProgress());
            }

            @Override
            public void onInformativeMessage(String message) {
                showInDialog("", message);
            }

            @Override
            public void onBannedOrgUnit() {
                showInDialog("", PreferencesState.getInstance().getContext().getString(
                        R.string.exception_org_unit_banned));
            }

            @Override
            public void onReOpenOrgUnit() {
                showInDialog("",
                        String.format(PreferencesState.getInstance().getContext().getString(
                                R.string.dialog_reopen_org_unit),
                                PreferencesState.getInstance().getOrgUnit()));
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                onError("PUSHUSECASE ERROR "+e.getMessage());
            }

            @Override
            public void onInvalidCredentials() {
                onError("PUSHUSECASE ERROR onInvalidCredentials");
            }

            @Override
            public void onClosedUser() {
                onError("PUSHUSECASE ERROR on closedUser "+PreferencesState.getInstance().isPushInProgress());
                if(DashboardActivity.dashboardActivity != null && DashboardActivity.dashboardActivity.isInForeground()) {
                    closeUserLogout();
                }
            }

            @Override
            public void onApiCallError() {
                onError("onApiCallError");
            }
        });
    }

    public void onError(String error) {
        Log.e(TAG, error);
        mPushService.onPushError(error);
    }

    public void showInDialog(String title, String message) {
        if(DashboardActivity.dashboardActivity != null && DashboardActivity.dashboardActivity.isInForeground()) {
            DashboardActivity.dashboardActivity.showException(DashboardActivity.dashboardActivity, title, message);
        }
    }

    public void closeUserLogout() {
        DashboardActivity.dashboardActivity.closeUserFromService(R.string.admin_announcement,
                PreferencesState.getInstance().getContext().getString(R.string.user_close));
    }
}
