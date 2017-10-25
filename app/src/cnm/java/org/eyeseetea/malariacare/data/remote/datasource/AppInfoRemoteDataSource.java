package org.eyeseetea.malariacare.data.remote.datasource;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.CnmApiClient;
import org.eyeseetea.malariacare.data.sync.importer.models.Version;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;


public class AppInfoRemoteDataSource implements IAppInfoRepository {
    @Override
    public AppInfo getAppInfo() {
        return null;
    }

    @Override
    public void getAppInfo(final IDataSourceCallback<AppInfo> callback) {
        CnmApiClient cnmApiClient = null;
        try {
            cnmApiClient = new CnmApiClient(PreferencesState.getInstance().getDhisURL() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
        cnmApiClient.getMetadataVersion(new CnmApiClient.CnmApiClientCallBack<Version>() {
            @Override
            public void onSuccess(Version result) {
                callback.onSuccess(new AppInfo(false, result.getVersion()));
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void saveAppInfo(AppInfo appInfo) {

    }
}
