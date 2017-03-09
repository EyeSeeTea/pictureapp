package org.eyeseetea.malariacare.domain.usecase.pull;

import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;

public class PullUseCase {

    public interface Callback {
        void onComplete();

        void onStep(PullStep step);

        void onError(String message);

        void onNetworkError();

        void onPullConversionError();

        void onCancel();
    }

    IPullController mPullController;

    public PullUseCase(IPullController pullController) {
        mPullController = pullController;
    }

    public void execute(PullFilters pullFilters, final Callback callback) {
        mPullController.pull(pullFilters, new IPullController.Callback() {
            @Override
            public void onComplete() {
                //TODO jsanchez create OrgUnitRepository and when pull finish
                //invoke remove current OrgUnit from here (only laos and cambodia)

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
                } else if (throwable instanceof PullConversionException) {
                    callback.onPullConversionError();
                } else {
                    callback.onError(throwable.getMessage());
                }
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }
        });
    }

    public void cancel() {
        mPullController.cancel();
    }
}
