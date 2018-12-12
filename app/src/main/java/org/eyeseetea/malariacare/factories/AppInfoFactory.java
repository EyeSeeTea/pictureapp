package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.usecase.GetAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveAppInfoUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class AppInfoFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    public GetAppInfoUseCase getGetAppInfoUseCase(Context context) {
        IAppInfoRepository appInfoRepository = new AppInfoDataSource(context);
        return new GetAppInfoUseCase(mainExecutor, asyncExecutor, appInfoRepository);
    }

    public SaveAppInfoUseCase getSaveAppInfoUseCase(Context context) {
        IAppInfoRepository appInfoRepository = new AppInfoDataSource(context);
        return new SaveAppInfoUseCase(mainExecutor, asyncExecutor, appInfoRepository);
    }

}
