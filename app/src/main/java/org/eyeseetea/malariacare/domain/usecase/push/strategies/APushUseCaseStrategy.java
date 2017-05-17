package org.eyeseetea.malariacare.domain.usecase.push.strategies;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
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
            configureBanOrgUnitChangeListener();

            boolean isBanned = mPushUseCase.isOrgUnitBanned();

            if (isBanned) {
                pushController.changePushInProgress(false);
            } else {
                mPushUseCase.runPush();
            }

        } catch (Exception e) {
            pushController.changePushInProgress(false);
            mPushUseCase.notifyPushError();
        }
    }

    private void configureBanOrgUnitChangeListener() {
        mPushUseCase.getOrganisationUnitRepository().setBanOrgUnitChangeListener(
                new IOrganisationUnitRepository.BanOrgUnitChangeListener() {
                    @Override
                    public void onBanOrgUnitChanged(OrganisationUnit organisationUnit) {
                        if (organisationUnit.isBanned()) {
                            mPushUseCase.notifyBannedOrgUnitError();
                        } else {
                            mPushUseCase.notifyReOpenOrgUnit();
                        }
                    }
                });
    }
}
