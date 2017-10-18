package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

public class GetAppInfoUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAppInfoRepository mAppInfoRepository;
    private Callback mCallback;

    public GetAppInfoUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAppInfoRepository appInfoRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAppInfoRepository = appInfoRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mMainExecutor.run(this);
    }

    @Override
    public void run() {
        final AppInfo appInfo = mAppInfoRepository.getAppInfo();

        notifyAppInfo(appInfo);
    }

    private void notifyAppInfo(final AppInfo appInfo) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onAppInfoLoaded(appInfo);
            }
        });
    }


    public interface Callback {
        void onAppInfoLoaded(AppInfo appInfo);
    }
}
