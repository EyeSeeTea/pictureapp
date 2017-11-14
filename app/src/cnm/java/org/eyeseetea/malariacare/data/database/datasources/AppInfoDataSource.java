package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesCNM;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;


public class AppInfoDataSource implements IAppInfoRepository {
    @Override
    public AppInfo getAppInfo() {
        return new AppInfo(getMetadataDownloaded(), getMetadataVersion());
    }

    @Override
    public void getAppInfo(IDataSourceCallback<AppInfo> callback) {
        callback.onSuccess(new AppInfo(getMetadataDownloaded(), getMetadataVersion()));
    }

    @Override
    public void saveAppInfo(AppInfo appInfo) {
        PreferencesCNM.setMetadataDownloaded(appInfo.isMetadataDownloaded());
        PreferencesCNM.saveMetadataVersion(appInfo.getMetadataVersion());
    }

    private boolean getMetadataDownloaded() {
        return PreferencesCNM.isMetadataDownloaded();
    }

    private String getMetadataVersion() {
        return PreferencesCNM.getMetadataVersion();
    }
}
