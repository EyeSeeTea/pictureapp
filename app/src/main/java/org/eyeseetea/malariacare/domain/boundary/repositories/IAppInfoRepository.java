package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

public interface IAppInfoRepository {
    AppInfo getAppInfo();

    void getAppInfo(IDataSourceCallback<AppInfo> callback);
    void saveAppInfo(AppInfo appInfo);
}
