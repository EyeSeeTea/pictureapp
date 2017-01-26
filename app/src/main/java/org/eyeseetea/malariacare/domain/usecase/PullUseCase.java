package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IPullController;

public class PullUseCase {

    public interface Callback {
        void onComplete();

        void onError(String message);
    }

    IPullController mPullController;

    public PullUseCase(IPullController pullController) {
        mPullController = pullController;
    }

    public void execute(final Callback callback) {
        mPullController.pull(new IPullController.Callback() {
            @Override
            public void onComplete() {
                callback.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
    }
}
