package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

import java.util.Date;

public class UpdateLastPushDateUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAppInfoRepository mAppInfoRepository;

    public UpdateLastPushDateUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAppInfoRepository appInfoRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAppInfoRepository = appInfoRepository;
    }

    public void execute() {
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        AppInfo appInfo = mAppInfoRepository.getAppInfo();
        appInfo = new AppInfo(appInfo.getMetadataVersion(), appInfo.getConfigFileVersion(),
                appInfo.getAppVersion(), appInfo.getUpdateMetadataDate(), new Date());
        mAppInfoRepository.saveAppInfo(appInfo);
    }
}
