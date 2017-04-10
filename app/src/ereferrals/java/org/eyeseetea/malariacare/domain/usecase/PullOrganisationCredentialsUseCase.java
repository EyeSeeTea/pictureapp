package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.sync.importer.PullOrganisationCredentialsController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;

public class PullOrganisationCredentialsUseCase implements UseCase {

    public interface Callback {
        void onComplete();

        void onError(String message);

        void onNetworkError();

        void onPullConversionError();
    }

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private PullOrganisationCredentialsController mPullOrganisationCredentialsController;

    private Callback mCallback;

    public PullOrganisationCredentialsUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            PullOrganisationCredentialsController pullOrganisationCredentialsController) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mPullOrganisationCredentialsController = pullOrganisationCredentialsController;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mPullOrganisationCredentialsController.pullOrganisationCredentials(
                new PullOrganisationCredentialsController.Callback() {
                    @Override
                    public void onComplete() {
                        notifyComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof NetworkException) {
                            notifyOnNetworkError();
                        } else if (throwable instanceof PullConversionException) {
                            notifyPullConversionError();
                        } else {
                            notifyError(throwable.getMessage());
                        }
                    }
                });
    }

    private void notifyComplete() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onComplete();
            }
        });
    }


    private void notifyOnNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    private void notifyPullConversionError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onPullConversionError();
            }
        });
    }

    private void notifyError(final String message) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(message);
            }
        });
    }

}
