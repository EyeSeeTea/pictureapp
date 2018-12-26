package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IApiStatusRepository;
import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;

import java.io.IOException;

public class GetWebAvailableUseCase implements UseCase {
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private IApiStatusRepository mApiStatusRepository;
    private Callback mCallback;

    public GetWebAvailableUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            IApiStatusRepository apiStatusRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mApiStatusRepository = apiStatusRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        ApiStatus apiStatus = null;
        try {
            apiStatus = mApiStatusRepository.getApiStatus();
        } catch (IOException e) {
            notifyOnError();
        } catch (AvailableApiException e) {
            notifyOnError();
        }
        if (apiStatus.isAvailable()) {
            notifyOnSuccess();
        } else {
            notifyOnError();
        }
    }

    private void notifyOnError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess();
            }
        });
    }

    private void notifyOnSuccess() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onError();
            }
        });
    }

    public interface Callback {
        void onSuccess();

        void onError();
    }
}
