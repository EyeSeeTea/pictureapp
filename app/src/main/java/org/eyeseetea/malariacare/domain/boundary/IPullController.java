package org.eyeseetea.malariacare.domain.boundary;

public interface IPullController {
    interface Callback {
        void onComplete();

        void onError(Throwable throwable);
    }

    void pull(Callback callback);
}
