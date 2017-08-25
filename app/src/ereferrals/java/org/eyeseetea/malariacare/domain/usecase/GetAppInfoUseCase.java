package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

public class GetAppInfoUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAppInfoRepository mAppInfoRepository;
    private Callback mCallback;

    public GetAppInfoUseCase(
            IMainExecutor mainExecutor,
            IAppInfoRepository appInfoRepository) {
        mMainExecutor = mainExecutor;
        mAppInfoRepository = appInfoRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mMainExecutor.run(this);
    }

    @Override
    public void run() {
        mCallback.onGetAppInfo(mAppInfoRepository.getAppInfo());
    }


    public interface Callback {
        void onGetAppInfo(AppInfo appInfo);
    }
}
