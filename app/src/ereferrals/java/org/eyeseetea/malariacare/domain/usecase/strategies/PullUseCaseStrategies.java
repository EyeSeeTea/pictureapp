package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.strategies.APullUseCaseStrategy;


public class PullUseCaseStrategies extends APullUseCaseStrategy {


    public PullUseCaseStrategies(
            PullUseCase pullUseCase) {
        super(pullUseCase);
    }

    @Override
    public void onPullComplete() {
        mPullUseCase.notifyComplete();
    }

    @Override
    public void onOnNetworkError() {
        if (PreferencesEReferral.getUserProgramId() != -1) {
            mPullUseCase.notifyComplete();
        } else {
            mPullUseCase.notifyOnNetworkError();
        }
    }
}
