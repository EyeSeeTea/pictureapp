package org.eyeseetea.malariacare.domain.usecase.pull.strategies;

import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;

public abstract class APullUseCaseStrategy {

    protected PullUseCase mPullUseCase;

    public APullUseCaseStrategy(PullUseCase pullUseCase) {
        mPullUseCase = pullUseCase;
    }

    public void onPullComplete() {
        mPullUseCase.notifyComplete();
    }

    public void onOnNetworkError() {
        mPullUseCase.notifyOnNetworkError();
    }
}
