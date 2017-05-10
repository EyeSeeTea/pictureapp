package org.eyeseetea.malariacare.domain.usecase.push.strategies;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;

public abstract class APushUseCaseStrategy {
    protected PushUseCase mPushUseCase;

    public APushUseCaseStrategy(PushUseCase pushUseCase) {
        mPushUseCase = pushUseCase;
    }

    public void run() {
        IPushController pushController = mPushUseCase.getPushController();

        if (pushController.isPushInProgress()) {
            mPushUseCase.notifyPushInProgressError();
            return;
        }

        pushController.changePushInProgress(true);

        try {
            Boolean isBanned = mPushUseCase.isOrgUnitBanned();

            OrgUnit orgUnit = OrgUnit.findByName(PreferencesState.getInstance().getOrgUnit());
            if (isBanned) {
                if (orgUnit != null && !orgUnit.isBanned()) {
                    orgUnit.setBan(true);
                    orgUnit.save();
                    mPushUseCase.notifyBannedOrgUnitError();

                }
                pushController.changePushInProgress(false);
            } else {
                if (orgUnit != null && orgUnit.isBanned()) {
                    orgUnit.setBan(false);
                    orgUnit.save();
                    mPushUseCase.notifyReOpenOrgUnit();
                }
                mPushUseCase.runPush();
            }

        } catch (NetworkException e) {
            mPushUseCase.mPushController.changePushInProgress(false);
            mPushUseCase.notifyNetworkError();
        } catch (ApiCallException e) {
            mPushUseCase.mPushController.changePushInProgress(false);
            mPushUseCase.notifyApiCallError(e);
        } catch (Exception e) {
            mPushUseCase.mPushController.changePushInProgress(false);
            mPushUseCase.notifyPushError();
        }
    }
}
