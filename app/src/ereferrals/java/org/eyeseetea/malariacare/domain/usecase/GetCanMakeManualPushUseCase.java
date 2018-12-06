package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

import java.util.Date;

public class GetCanMakeManualPushUseCase implements UseCase {

    private static final long MIN_TIME_CAN_PUSH = 30000;

    public interface Callback {
        void onSuccess(boolean canMakePush);
    }

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAppInfoRepository mAppInfoRepository;
    private Callback mCallback;

    public GetCanMakeManualPushUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAppInfoRepository appInfoRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAppInfoRepository = appInfoRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        AppInfo appInfo = mAppInfoRepository.getAppInfo();
        Date now = new Date();
        boolean canMakePush =
                now.getTime() - appInfo.getLastPushDate().getTime() > MIN_TIME_CAN_PUSH;
        notifyOnSuccess(canMakePush);
    }

    private void notifyOnSuccess(final boolean canMakePush) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(canMakePush);
            }
        });
    }
}
