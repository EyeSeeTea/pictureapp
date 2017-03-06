package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.network.SurveyChecker;

public class PushUseCase {

    public interface Callback {
        void onComplete();

        void onPushError();

        void onPushInProgressError();

        void onSurveysNotFoundError();

        void onConversionError();

        void onNetworkError();
    }

    private IPushController mPushController;

    public PushUseCase(IPushController pushController) {
        mPushController = pushController;
    }

    public void execute(final Callback callback) {
        if (mPushController.isPushInProgress()) {
            callback.onPushInProgressError();
            return;
        }

        mPushController.changePushInProgress(true);

        mPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onComplete() {
                mPushController.changePushInProgress(false);

                callback.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                mPushController.changePushInProgress(false);

                if (throwable instanceof NetworkException) {
                    callback.onNetworkError();
                } else if (throwable instanceof ConversionException) {
                    callback.onConversionError();
                } else if (throwable instanceof SurveysToPushNotFoundException) {
                    callback.onSurveysNotFoundError();
                } else {
                    callback.onPushError();
                }
            }
        });
    }

}

