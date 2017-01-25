package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;

public interface IPullController {
    interface Callback {
        void onComplete();

        void onStep(PullStep step);
        void onError(Throwable throwable);
    }

    void pull(Callback callback);
}
