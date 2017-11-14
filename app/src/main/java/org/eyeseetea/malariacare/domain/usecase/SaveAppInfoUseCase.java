package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

public class SaveAppInfoUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAppInfoRepository mAppInfoRepository;
    private Callback mCallback;
    private AppInfo mAppInfo;


    public SaveAppInfoUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAppInfoRepository appInfoRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAppInfoRepository = appInfoRepository;
    }

    public void excute(Callback callback, AppInfo appInfo) {
        mCallback = callback;
        mAppInfo = appInfo;
        mMainExecutor.run(this);
    }

    @Override
    public void run() {
        mAppInfoRepository.saveAppInfo(mAppInfo);
        mAsyncExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onAppInfoSaved();
            }
        });
    }

    public interface Callback {
        void onAppInfoSaved();
    }
}
