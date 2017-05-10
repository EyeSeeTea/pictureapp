package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.strategies.APushUseCaseStrategy;

public class PushUseCaseStrategy extends APushUseCaseStrategy {
    public PushUseCaseStrategy(PushUseCase pushUseCase) {
        super(pushUseCase);
    }

    @Override
    public void run() {
        IPushController pushController = mPushUseCase.getPushController();

        if (pushController.isPushInProgress()) {
            mPushUseCase.notifyPushInProgressError();
        } else {
            pushController.changePushInProgress(true);
            mPushUseCase.runPush();
        }
    }
}
