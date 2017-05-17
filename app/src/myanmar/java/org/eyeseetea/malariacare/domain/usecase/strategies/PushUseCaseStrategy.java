package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.strategies.APushUseCaseStrategy;

public class PushUseCaseStrategy extends APushUseCaseStrategy {
    public PushUseCaseStrategy(PushUseCase pushUseCase,
            IPushController pushController,
            IOrganisationUnitRepository organisationUnitRepository) {
        super(pushUseCase, pushController, organisationUnitRepository);
    }


}
