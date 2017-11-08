package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IPhoneFormatRepository;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

public class GetPhoneFormatUseCase implements UseCase {
    private IPhoneFormatRepository mPhoneFormatLocalDataSource;
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public GetPhoneFormatUseCase(
            IPhoneFormatRepository phoneFormatLocalDataSource,
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor) {
        mPhoneFormatLocalDataSource = phoneFormatLocalDataSource;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        final PhoneFormat phoneFormat = mPhoneFormatLocalDataSource.getUserPhoneFormat();
        if (phoneFormat != null) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess(phoneFormat);
                }
            });
        } else {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            });
        }
    }

    public interface Callback {
        void onSuccess(PhoneFormat phoneFormat);

        void onError();
    }

}
