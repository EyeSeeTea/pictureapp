package org.eyeseetea.malariacare.domain.usecase.pull;

import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class PullUseCase {

    public interface Callback {
        void onComplete();

        void onStep(PullStep step);

        void onError(String message);

        void onNetworkError();
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
            public void onStep(PullStep step) {
                callback.onStep(step);
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof NetworkException) {
                    callback.onNetworkError();
                } else {
                    callback.onError(throwable.getMessage());
                }
            }
        });
    }
}
