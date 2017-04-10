package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.strategies.APullUseCaseStrategy;


public class PullUseCaseStrategies extends APullUseCaseStrategy {
    public PullUseCaseStrategies(
            PullUseCase pullUseCase) {
        super(pullUseCase);
    }

}
