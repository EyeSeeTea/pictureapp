package org.eyeseetea.malariacare.domain.usecase.push.strategies;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;

public abstract class APushUseCaseStrategy {
    protected PushUseCase mPushUseCase;
    protected IPushController mPushController;
    protected IOrganisationUnitRepository mOrganisationUnitRepository;

    public APushUseCaseStrategy(PushUseCase pushUseCase,
            IPushController pushController,
            IOrganisationUnitRepository organisationUnitRepository) {
        mPushUseCase = pushUseCase;
        mPushController = pushController;
        mOrganisationUnitRepository = organisationUnitRepository;
    }


    public void run() {
        IPushController pushController = mPushController;

        if (pushController.isPushInProgress()) {
            mPushUseCase.notifyPushInProgressError();
            return;
        }

        pushController.changePushInProgress(true);

        try {
            configureBanOrgUnitChangeListener();

            boolean isBanned = isOrgUnitBanned();

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

    private boolean isOrgUnitBanned() throws NetworkException, ApiCallException {
        OrganisationUnit orgUnit = null;
        try {
            orgUnit = mOrganisationUnitRepository.getCurrentOrganisationUnit(ReadPolicy.REMOTE);
        } catch (NetworkException e) {
            mPushController.changePushInProgress(false);
            mPushUseCase.notifyNetworkError();
        } catch (ApiCallException e) {
            mPushController.changePushInProgress(false);
            mPushUseCase.notifyApiCallError(e);
        }
        return orgUnit.isBanned();
    }

}
