package org.eyeseetea.malariacare.domain.usecase.pull;

import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.strategies.APullUseCaseStrategy;
import org.eyeseetea.malariacare.domain.usecase.strategies.PullUseCaseStrategies;

public class PullUseCase implements UseCase {

    public interface Callback {
        void onComplete();

        void onStep(PullStep step);

        void onError(String message);

        void onNetworkError();

        void onPullConversionError();

        void onCancel();
    }

    private IPullController mPullController;
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    private PullFilters mPullFilters;
    private Callback mCallback;

    private APullUseCaseStrategy mPullUseCaseStrategy;

    public PullUseCase(IPullController pullController, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor) {
        mPullController = pullController;
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mPullUseCaseStrategy = new PullUseCaseStrategies(this);
    }

    public void execute(PullFilters pullFilters, final Callback callback) {
        mPullFilters = pullFilters;
        mCallback = callback;

        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mPullController.pull(mPullFilters, new IPullController.Callback() {
            @Override
            public void onComplete() {
                //TODO jsanchez create OrgUnitRepository and when pull finish
                //invoke remove current OrgUnit from here (only laos and cambodia)

                mPullUseCaseStrategy.onPullComplete();
            }

            @Override
            public void onStep(PullStep step) {
                notifyStep(step);
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof NetworkException) {
                    mPullUseCaseStrategy.onOnNetworkError();
                } else if (throwable instanceof PullConversionException) {
                    notifyPullConversionError();
                } else {
                    notifyError(throwable.getMessage());
                }
            }

            @Override
            public void onCancel() {
                mCallback.onCancel();
            }
        });
    }

    public void cancel() {
        mPullController.cancel();
    }

    public void notifyComplete() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onComplete();
            }
        });
    }

    private void notifyStep(final PullStep step) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onStep(step);
            }
        });

    }

    public void notifyOnNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    public void notifyPullConversionError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onPullConversionError();
            }
        });
    }

    public void notifyError(final String message) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(message);
            }
        });
    }
}
