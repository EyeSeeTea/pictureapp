package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;

public interface IPullController {
    interface Callback {
        void onComplete();

        void onCancel();
        void onStep(PullStep step);
        void onError(Throwable throwable);
    }

    void pull(PullFilters pullFilters, Callback callback);

    void cancel();
}
